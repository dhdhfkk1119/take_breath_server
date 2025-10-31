package com.take.take_breath.record;

public enum FileType {
    IMAGE("images"),
    AUDIO("audios"),
    VIDEO("videos");

    private final String directory;

    FileType(String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }
}
