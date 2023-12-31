# MIDI Reader for Raft Piano

This MIDI Reader application is designed to read MIDI files and simulate playing them on the piano in the game "Raft". It converts MIDI notes to corresponding keyboard key presses, handling octave shifts and note durations to recreate the music on the virtual piano.

## Features

- Reads MIDI files and processes note events.
- Simulates key presses corresponding to piano notes in Raft.
- Handles octave shifts and note durations.
- Adjustable to work with different MIDI file structures.

## Requirements

- Java (JDK 8 or higher) 
- Raft

## Usage

Run the application from the command line, providing the path to a MIDI file as the first argument and the tempo of the MIDI file as the second argument. For example, to play the MIDI file `twinkle.mid` at 85 beats per minute, run the following command:

```bash
java -jar midi-raft-player-1.0.0.jar twinkle.mid 85
```

The application waits for 5 seconds before starting to play the MIDI file. This delay allows time to switch focus to the Raft game window for the key presses to register correctly.

## Configuration

The key mappings for the Raft piano can be adjusted in the `RaftPianoPlayer` class. Modify the `noteToKeyMap` initialization in this class to change the mappings between MIDI notes and keyboard keys.

## Notes and Best Practices

- Ensure the Raft game window is active and the piano interface is open when running this application.
- The timing accuracy of note playback may vary based on system performance and Java's capabilities.

## Contributing

You are free to edit, modify, and distribute this project as you see fit. We welcome contributions and improvements to the code. Feel free to fork the repository, make changes, and submit pull requests. For major changes, please open an issue first to discuss what you would like to change.
