package com.cookandroid.project_energizor.view;

import java.util.HashMap;
import java.util.Map;

public class Beacon {

    public String BeaconUUID;
    public String BeaconMAC;

    public Beacon() {}

    public Beacon(String BeaconUUID, String BeaconMAC) {
        this.BeaconUUID = BeaconUUID;
        this.BeaconMAC = BeaconMAC;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("BeaconUUID", BeaconUUID);
        result.put("BeaconMAC", BeaconMAC);

        return result;
    }
}
