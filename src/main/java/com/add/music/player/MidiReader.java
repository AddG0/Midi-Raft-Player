package com.add.music.player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sound.midi.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MidiReader {
    public static class NoteEvent {
        int key;
        float startTime;
        long duration;

        NoteEvent(int key, float startTime, long duration) {
            this.key = key;
            this.startTime = startTime;
            this.duration = duration;
        }
    }

    private List<NoteEvent> noteEvents = new ArrayList<>();
    private RaftPianoPlayer raftPianoPlayer;

    public MidiReader() {
        try {
            raftPianoPlayer = new RaftPianoPlayer();
        } catch (Exception e) {
            log.error("Error initializing RaftPianoPlayer", e);
        }
    }

    public void readMidiFile(String filePath, float tempo) {
        try {
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();
            Sequence sequence = MidiSystem.getSequence(new File(filePath));

            int resolution = sequence.getResolution();
            float microsecondsPerBeat = 60000000 / tempo;
            float millisecondsPerTick = microsecondsPerBeat / (resolution * 1000);

            // Pre-process the MIDI file
            for (Track track : sequence.getTracks()) {
                Map<Integer, Long> noteOnTimes = new HashMap<>();
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    MidiMessage message = event.getMessage();

                    if (message instanceof ShortMessage) {
                        ShortMessage sm = (ShortMessage) message;
                        int key = sm.getData1();

                        if (sm.getCommand() == ShortMessage.NOTE_ON && sm.getData2() > 0) {
                            noteOnTimes.put(key, event.getTick());
                        } else if (sm.getCommand() == ShortMessage.NOTE_OFF
                                || (sm.getCommand() == ShortMessage.NOTE_ON && sm.getData2() == 0)) {
                            Long noteOnTime = noteOnTimes.get(key);
                            if (noteOnTime != null) {
                                long durationInTicks = event.getTick() - noteOnTime;
                                long durationInMillis = (long) (durationInTicks * millisecondsPerTick);
                                noteEvents.add(new NoteEvent(key, noteOnTime * millisecondsPerTick, durationInMillis));
                                noteOnTimes.remove(key);
                            }
                        }
                    }
                }
            }

            raftPianoPlayer.playNotes(noteEvents);

        } catch (Exception e) {
            log.error("Error processing MIDI file", e);
        }
    }

}
