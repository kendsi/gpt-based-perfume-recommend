package com.acscent.chatdemo2.repository;

import com.acscent.chatdemo2.model.User;

import com.acscent.chatdemo2.model.Image;

public interface GoogleDriveRepository {
    public void save(String userCode);
    public void save(Image image);
    public User findByCode(String userCode);
}
