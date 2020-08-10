package hu.szte.richard.minesweeper;

import android.widget.ImageView;

class Field {
    private ImageView picture;
    private boolean locked;
    private boolean hasMine;
    private boolean revealed;

    boolean isRevealed() {
        return revealed;
    }

    void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    ImageView getPicture() {
        return picture;
    }

    boolean isLocked() {
        return locked;
    }

    boolean hasMine() {
        return hasMine;
    }

    void setPicture(ImageView picture) {
        this.picture = picture;
    }

    void setLocked(boolean locked) {
        this.locked = locked;
    }

    void setHasMine(boolean hasMine) {
        this.hasMine = hasMine;
    }
}
