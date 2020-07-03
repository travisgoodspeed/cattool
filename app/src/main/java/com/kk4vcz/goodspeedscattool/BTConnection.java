package com.kk4vcz.goodspeedscattool;

import com.kk4vcz.codeplug.RadioConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* This class wraps an Android RFCOMM Bluetooth socket for use with the generic codeplugtool
 * library, so that the radio drivers can be written on a Linux desktop well away from a phone.
 */

public class BTConnection implements RadioConnection {

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public int setBaudRate(int i) {
        //Bluetooth connecitons have no baud rate.
        return 0;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void close() throws IOException {

    }
}
