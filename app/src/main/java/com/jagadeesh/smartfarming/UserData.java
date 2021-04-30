package com.jagadeesh.smartfarming;

public class UserData {

    public String fullName, email, password, writeAPIkey, readAPIkey, channelId, minTemp, maxTemp;

    public boolean TempLimitState;

    public UserData() {

    }

    public UserData(String _fullName, String _email, String _password,
                    String _writeAPIkey, String _readAPIkey, String _channelId,
                    String _minTemp, String _maxTemp, boolean _TempLimitState) {
        this.fullName = _fullName;
        this.email = _email;
        this.password = _password;
        this.writeAPIkey = _writeAPIkey;
        this.readAPIkey = _readAPIkey;
        this.channelId = _channelId;
        this.minTemp = _minTemp;
        this.maxTemp = _maxTemp;
        this.TempLimitState = _TempLimitState;
    }
}
