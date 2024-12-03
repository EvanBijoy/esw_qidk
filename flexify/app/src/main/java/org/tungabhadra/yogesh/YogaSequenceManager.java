package org.tungabhadra.yogesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YogaSequenceManager {
    private List<YogaPose> currentSequence;
    private int currentPoseIndex = 0;

    public static class YogaPose {
        private final String name;
        private final int index;
        private final int durationSeconds;

        public YogaPose(String name, int index, int durationSeconds) {
            this.name = name;
            this.index = index;
            this.durationSeconds = durationSeconds;
        }

        public String getName() { return name; }
        public int getIndex() { return index; }
        public int getDurationSeconds() { return durationSeconds; }
    }

    public YogaSequenceManager(String sequenceType) {
        initializeSequence(sequenceType);
    }

    private void initializeSequence(String sequenceType) {
        currentSequence = new ArrayList<>();

        switch (sequenceType) {
            case "Suryanamaskara":
                // Traditional Surya Namaskar sequence
                currentSequence = Arrays.asList(
                        new YogaPose("Pranamasana", 15, 30),           // Prayer Pose
                        new YogaPose("Padhahastasana", 4, 30),         // Hand to Foot Pose
                        new YogaPose("Ashwasanchalasana", 5, 30),      // Equestrian Pose
                        new YogaPose("Astangasana", 6, 30),            // Eight Limbed Pose
                        new YogaPose("Bhujangasana", 7, 30),           // Cobra Pose
                        new YogaPose("Parvathasana", 9, 30),           // Mountain Pose
                        new YogaPose("Ashwasanchalasana", 5, 30),      // Equestrian Pose
                        new YogaPose("Padhahastasana", 4, 30),         // Hand to Foot Pose
                        new YogaPose("Pranamasana", 15, 30)            // Prayer Pose
                );
                break;

            case "Yoga Set 1":
                // Standing poses focused sequence
                currentSequence = Arrays.asList(
                        new YogaPose("Pranamasana", 15, 30),           // Prayer Pose
                        new YogaPose("Vrukshasana", 11, 45),           // Tree Pose
                        new YogaPose("Trikonasana", 2, 45),            // Triangle Pose
                        new YogaPose("Veerabhadrasana", 3, 45),        // Warrior Pose
                        new YogaPose("ArdhaChandrasana", 8, 45),       // Half Moon Pose
                        new YogaPose("Utkatakonasana", 0, 45),         // Chair Pose
                        new YogaPose("Natarajasana", 1, 45),           // Dancer Pose
                        new YogaPose("Pranamasana", 15, 30)            // Prayer Pose
                );
                break;

            case "Yoga Set 2":
                // Floor poses focused sequence
                currentSequence = Arrays.asList(
                        new YogaPose("Pranamasana", 15, 30),           // Prayer Pose
                        new YogaPose("Dandasana", 12, 45),             // Staff Pose
                        new YogaPose("BaddhaKonasana", 10, 45),        // Butterfly Pose
                        new YogaPose("Shashangasana", 13, 45),         // Rabbit Pose
                        new YogaPose("Bhujangasana", 7, 45),           // Cobra Pose
                        new YogaPose("Ardhachakrasana", 14, 45),       // Half Wheel Pose
                        new YogaPose("Pranamasana", 15, 30)            // Prayer Pose
                );
                break;
        }
    }

    public YogaPose getCurrentPose() {
        if (currentSequence.isEmpty() || currentPoseIndex >= currentSequence.size()) {
            return null;
        }
        return currentSequence.get(currentPoseIndex);
    }

    public YogaPose getNextPose() {
        if (currentPoseIndex + 1 < currentSequence.size()) {
            return currentSequence.get(currentPoseIndex + 1);
        }
        return null;
    }

    public boolean moveToNextPose() {
        if (currentPoseIndex + 1 < currentSequence.size()) {
            currentPoseIndex++;
            return true;
        }
        return false;
    }

    public boolean isSequenceComplete() {
        return currentPoseIndex >= currentSequence.size() - 1;
    }

    public int getTotalPoses() {
        return currentSequence.size();
    }

    public int getCurrentPoseNumber() {
        return currentPoseIndex + 1;
    }

    public void resetSequence() {
        currentPoseIndex = 0;
    }
}