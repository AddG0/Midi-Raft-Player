package com.add.music.player;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.add.music.player.MidiReader.NoteEvent;

public class RaftPianoPlayer {
    private Robot robot;
    private Map<Integer, List<Integer>> noteToKeyMap;
    private Map<Integer, Long> pressedKeysReleaseTime = new HashMap<>();
    private static final int octaveShiftUpKey = KeyEvent.VK_SHIFT;
    private static final int octaveShiftDownKey = KeyEvent.VK_SPACE;

    public RaftPianoPlayer() throws AWTException {
        robot = new Robot();
        initializeNoteMappings();
    }

    private void initializeNoteMappings() {
        noteToKeyMap = new HashMap<>();

        addNoteMap(48, octaveShiftDownKey, KeyEvent.VK_1); // C3
        addNoteMap(50, octaveShiftDownKey, KeyEvent.VK_2); // D3
        addNoteMap(52, octaveShiftDownKey, KeyEvent.VK_3); // E3
        addNoteMap(53, octaveShiftDownKey, KeyEvent.VK_4); // F3
        addNoteMap(55, octaveShiftDownKey, KeyEvent.VK_5); // G3
        addNoteMap(57, octaveShiftDownKey, KeyEvent.VK_6); // A3
        addNoteMap(59, octaveShiftDownKey, KeyEvent.VK_7); // B3
        addNoteMap(60, KeyEvent.VK_1); // C4
        addNoteMap(62, KeyEvent.VK_2); // D4
        addNoteMap(64, KeyEvent.VK_3); // E4
        addNoteMap(65, KeyEvent.VK_4); // F4
        addNoteMap(67, KeyEvent.VK_5); // G4
        addNoteMap(69, KeyEvent.VK_6); // A4
        addNoteMap(71, KeyEvent.VK_7); // B4
        addNoteMap(72, KeyEvent.VK_8); // C5
        addNoteMap(74, KeyEvent.VK_9); // D5
        addNoteMap(76, KeyEvent.VK_0); // E5
        addNoteMap(77, octaveShiftUpKey, KeyEvent.VK_4); // F5
        addNoteMap(79, octaveShiftUpKey, KeyEvent.VK_5); // G5
        addNoteMap(81, octaveShiftUpKey, KeyEvent.VK_6); // A5
        addNoteMap(83, octaveShiftUpKey, KeyEvent.VK_7); // B5
        addNoteMap(84, octaveShiftUpKey, KeyEvent.VK_8); // C6
        addNoteMap(86, octaveShiftUpKey, KeyEvent.VK_9); // D6
        addNoteMap(88, octaveShiftUpKey, KeyEvent.VK_0); // E6
    }

    private void addNoteMap(int midiNote, Integer... keys) {
        noteToKeyMap.put(midiNote, Arrays.asList(keys));
    }

    public void playNotes(List<NoteEvent> noteEvents) {
        // Sort the noteEvents by their start times
        noteEvents.sort(Comparator.comparingDouble(o -> o.startTime));

        long startTime = System.nanoTime();

        for (NoteEvent noteEvent : noteEvents) {
            while (true) {
                long currentTime = System.nanoTime() - startTime;

                // Convert nanoseconds to milliseconds for comparison
                if (currentTime / 1_000_000 >= noteEvent.startTime) {
                    playKey(noteEvent.key, noteEvent.duration);
                    break;
                }

                releaseKeysIfNeeded();

                try {
                    Thread.sleep(1); // Sleep for a short duration to reduce CPU usage
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Interrupted while playing notes");
                    return;
                }
            }
        }
    }

    public void playKey(int midiNote, long duration) {
        // Current time in milliseconds
        long currentTime = System.currentTimeMillis();

        // Get the keyEvent or keyEvents (including octave shifts if any)
        List<Integer> keyEvents = noteToKeyMap.get(midiNote);
        if (keyEvents != null) {
            // Handle octave shift keys separately
            List<Integer> octaveShiftKeys = new ArrayList<>();

            for (Integer keyEvent : keyEvents) {
                // Check if the key is an octave shift key
                if (keyEvent.equals(octaveShiftUpKey) || keyEvent.equals(octaveShiftDownKey)) {
                    octaveShiftKeys.add(keyEvent);
                    robot.keyPress(keyEvent);
                } else {
                    // Check if the key is already pressed
                    if (pressedKeysReleaseTime.containsKey(keyEvent)) {
                        robot.keyRelease(keyEvent);
                    }
                    // Press the note key and set its release time
                    robot.keyPress(keyEvent);
                    pressedKeysReleaseTime.put(keyEvent, currentTime + duration);
                }
            }

            // Immediately release octave shift keys
            for (Integer octaveShiftKey : octaveShiftKeys) {
                robot.keyRelease(octaveShiftKey);
            }

            // if (!octaveShiftKeys.isEmpty()) {
            //     try {
            //         Thread.sleep(100); // Sleep for a short duration to reduce CPU usage
            //     } catch (InterruptedException e) {
            //         Thread.currentThread().interrupt();
            //         System.err.println("Interrupted while playing notes");
            //     }
            // }
        }
    }

    public void releaseKeysIfNeeded() {
        long currentTime = System.currentTimeMillis();
        List<Integer> keysToRelease = new ArrayList<>();

        // Check for keys to release
        for (Map.Entry<Integer, Long> entry : pressedKeysReleaseTime.entrySet()) {
            if (currentTime >= entry.getValue()) {
                robot.keyRelease(entry.getKey());
                keysToRelease.add(entry.getKey());
            }
        }

        // Remove released keys from the map
        for (Integer key : keysToRelease) {
            pressedKeysReleaseTime.remove(key);
        }
    }

}
