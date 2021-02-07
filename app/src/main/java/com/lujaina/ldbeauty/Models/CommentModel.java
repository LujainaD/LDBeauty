package com.lujaina.ldbeauty.Models;

import android.widget.RatingBar;

import java.io.Serializable;

public class CommentModel implements Serializable {
    private float numStars;
    private String comment;
    private String commentId;
    private String clientId;
    private String ownerId;
    private String commentDate;
    private boolean isShrink = true;
    private boolean showMenu = false;

    public boolean isShowMenu() {
        return showMenu;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }

    public float getNumStars() {
        return numStars;
    }

    public void setNumStars(float numStars) {
        this.numStars = numStars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public boolean isShrink() {
        return isShrink;
    }

    public void setShrink(boolean shrink) {
        isShrink = shrink;
    }
}
