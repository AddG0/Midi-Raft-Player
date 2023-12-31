package com.add;

import com.add.music.player.MidiReader;

public class Main {
    private static MidiReader midiReader = new MidiReader();

    public static void main(String... args) {
        // Check if a file path is provided as a command-line argument
        if (args.length > 0) {
            String midiFilePath = args[0];

            // Optional: Wait for a few seconds before starting (for setup)
            try {
                Thread.sleep(5000);  // Wait for 5 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
                // If interrupted, we might still want to proceed, or you can add a return statement here
            }

            // Read and process the specified MIDI file
            midiReader.readMidiFile(midiFilePath);
        } else {
            System.out.println("Please provide a MIDI file path as an argument.");
        }
    }
}
