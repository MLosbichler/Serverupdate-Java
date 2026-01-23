package com.dvhaus;

public class HostsEntry {
    private final String ip;
    private final String hostname;

    public HostsEntry(final String ip, final String hostname) {
        this.ip = ip;
        this.hostname = hostname;
    }

    public String getIp() {
        return this.ip;
    }

    public String getHostname() {
        return this.hostname;
    }
}
