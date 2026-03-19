package level1;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public final class Assets {
	
	private static Clip sfxLoopClip;

    private Assets() {}

    // --- NEW: one shared clip for background/menu music ---
    private static Clip loopingClip;

    public static BufferedImage loadImage(String resourceName) {
        String path = "/" + resourceName;
        try (InputStream in = Assets.class.getResourceAsStream(path)) {
            if (in == null) throw new IOException("Missing resource: " + path);
            return ImageIO.read(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image: " + path, e);
        }
    }

    /** Plays a short WAV clip from resources (non-blocking). */
    public static void playWav(String resourceName) {
        String path = "/" + resourceName;

        new Thread(() -> {
            try {
                URL url = Assets.class.getResource(path);
                System.out.println("Audio resource URL: " + url);

                if (url == null) {
                    throw new IOException("Missing resource on classpath: " + path
                            + "  (Make sure " + resourceName + " is in a SOURCE folder like /resources)");
                }

                try (AudioInputStream ais = AudioSystem.getAudioInputStream(url)) {
                    Clip clip = AudioSystem.getClip();
                    clip.open(ais);
                    clip.start();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, "wav-player").start();
    }

    public static void playLoopingWav(String resourceName) {
        String path = "/" + resourceName;

        new Thread(() -> {
            try {
                stopLoopingWav();

                java.net.URL url = Assets.class.getResource(path);
                System.out.println("Looping audio URL: " + url);
                if (url == null) throw new IOException("Missing resource on classpath: " + path);

                try (InputStream raw = url.openStream();
                     InputStream buf = new java.io.BufferedInputStream(raw);
                     AudioInputStream original = AudioSystem.getAudioInputStream(buf)) {

                    AudioFormat base = original.getFormat();
                    System.out.println("AudioFormat: " + base);

                    // Target format Java Clip almost always supports:
                    // 16-bit PCM_SIGNED, same sample rate, same channels
                    AudioFormat target = new AudioFormat(
                            AudioFormat.Encoding.PCM_SIGNED,
                            base.getSampleRate(),          // keep 48000 or 44100
                            16,                            // 16-bit
                            base.getChannels(),            // keep stereo/mono
                            base.getChannels() * 2,        // frameSize = channels * 2 bytes
                            base.getSampleRate(),
                            false                          // little-endian
                    );

                    AudioInputStream pcmStream = original;

                    // Convert only if needed
                    if (!AudioSystem.isConversionSupported(target, base)) {
                        throw new UnsupportedAudioFileException(
                                "Cannot convert from " + base + " to " + target
                        );
                    }

                    pcmStream = AudioSystem.getAudioInputStream(target, original);

                    DataLine.Info info = new DataLine.Info(Clip.class, target);
                    System.out.println("Line supported? " + AudioSystem.isLineSupported(info));

                    loopingClip = (Clip) AudioSystem.getLine(info);
                    loopingClip.open(pcmStream);
                    loopingClip.loop(Clip.LOOP_CONTINUOUSLY);
                    loopingClip.start();

                    System.out.println("Loop clip started. Running=" + loopingClip.isRunning());

                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, "wav-loop-player").start();
    }


    /** NEW: Stops the looping WAV (menu/splash music). */
    public static void stopLoopingWav() {
        try {
            if (loopingClip != null) {
                loopingClip.stop();
                loopingClip.close();
                loopingClip = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void playLoopingSfxWav(String resourceName) {
        String path = "/" + resourceName;

        new Thread(() -> {
            try {
                stopLoopingSfxWav();

                java.net.URL url = Assets.class.getResource(path);
                if (url == null) throw new IOException("Missing resource on classpath: " + path);

                try (InputStream raw = url.openStream();
                     InputStream buf = new java.io.BufferedInputStream(raw);
                     AudioInputStream original = AudioSystem.getAudioInputStream(buf)) {

                    AudioFormat base = original.getFormat();

                    AudioFormat target = new AudioFormat(
                            AudioFormat.Encoding.PCM_SIGNED,
                            base.getSampleRate(),
                            16,
                            base.getChannels(),
                            base.getChannels() * 2,
                            base.getSampleRate(),
                            false
                    );

                    AudioInputStream pcmStream = AudioSystem.getAudioInputStream(target, original);

                    DataLine.Info info = new DataLine.Info(Clip.class, target);
                    sfxLoopClip = (Clip) AudioSystem.getLine(info);
                    sfxLoopClip.open(pcmStream);
                    sfxLoopClip.loop(Clip.LOOP_CONTINUOUSLY);
                    sfxLoopClip.start();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, "wav-sfx-loop-player").start();
    }

    public static void stopLoopingSfxWav() {
        try {
            if (sfxLoopClip != null) {
                sfxLoopClip.stop();
                sfxLoopClip.close();
                sfxLoopClip = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
