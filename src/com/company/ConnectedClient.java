package com.company;

import com.company.workers.PassiveConnection;

import java.io.*;
import java.net.Socket;

/**
 * Created by LogiX on 2016-02-19.
 */
public class ConnectedClient implements Runnable {
    private Socket clientSocket;
    private OutputStreamWriter out;
    private BufferedReader in;
    private boolean isLoggedIn = false;
    private boolean usernameVerified = false;
    private PassiveConnection passiveConnection;
    public String workingDir;

    public ConnectedClient(Socket clientSocket) throws IOException {
        this.workingDir = (System.getProperty("user.dir") + "/shared").replace("\\", "/");
        this.out = new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8");
        this.clientSocket = clientSocket;
        write("220 (elHeffeFTPPro+++ By LanfeaR - 2k)");
    }

    public void write(String s) throws IOException {
        out.write(s + "\r\n");
        out.flush();
    }

    public void disconnect() throws IOException {
        if (this.passiveConnection.getDataSocket() != null) {
            this.passiveConnection.getDataSocket().close();
        }
        in.close();
        out.close();
        clientSocket.close();
    }

    @Override
    public void run() {
        try {
            InputHelper helper = new InputHelper(this);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                helper.processInput(line);
            }
        } catch (IOException e) {
            System.out.println("Client has disconnected.");
        }
        finally {
            try {
                clientSocket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public boolean hasPassiveConnection() throws IOException {
        return this.getPassiveConnection() != null && this.getPassiveConnection().getDataSocket() != null;
    }

    public boolean isLoggedIn() {
        return this.isLoggedIn;
    }
    public void setIsLoggedIn(boolean b) {
        this.isLoggedIn = b;
    }
    public boolean isUsernameVerified() {
        return usernameVerified;
    }
    public void setUsernameVerified(boolean usernameVerified) {
        this.usernameVerified = usernameVerified;
    }
    public PassiveConnection getPassiveConnection() {
        return passiveConnection;
    }
    public void setPassiveConnection(PassiveConnection passiveConnection) {
        this.passiveConnection = passiveConnection;
    }
    public String getWorkingDir() {
        return workingDir;
    }
    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir.replace("\\", "");
    }
}
