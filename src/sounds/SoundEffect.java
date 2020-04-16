package sounds;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class SoundEffect implements Runnable {
	private volatile boolean play = false;
	private AudioInputStream din = null;
	private SourceDataLine line = null;
	private Thread playThread;
	private URL file;
	
	private boolean loop = false;
	
    public SoundEffect(String fileName) {
    	System.out.println(String.format("Loading sound %s", fileName));
    	file = getClass().getResource(fileName);
    	if (file == null) {
    		System.out.println(String.format("Can't find sound file %s", fileName));
    		System.exit(1);
    	}
		playThread = new Thread(this);
    }
    
    private boolean load() {
    	try {
			AudioInputStream in = AudioSystem.getAudioInputStream(file);
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
					baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
					false);
			din = AudioSystem.getAudioInputStream(decodedFormat, in);
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
			line = (SourceDataLine) AudioSystem.getLine(info);
			
			if (line != null) {
				line.open(decodedFormat);
			} else {
				return false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    	
    	return true;
    }
    
    public void play() {
    	playThread.start();
    }
    
    public void stop() {
    	play = false;
    }

	public void run() {
		int nbRead = 0;
    	byte[] data = new byte[4096];
    	
    	do {
    		if (!load()) {
    			break;
    		}

			line.start();
			play = true;
	
			try {
				while (play && (nbRead = din.read(data, 0, data.length)) != -1) {	
					line.write(data, 0, nbRead);
				}
				line.drain();
				line.stop();
				line.close();
				din.close();
			
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
	
			} finally {
				if (din != null) {
					try {
						din.close();
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			}
    	} while (play && loop);
	}

	public boolean isLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}
}
