import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import json
from bluepy.btle import Scanner, DefaultDelegate
from datetime import datetime
import RPi.GPIO as GPIO
from gpiozero import RGBLED

# LED Setting
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
LED = RGBLED(red=21, green=20, blue=16) #GPIO No, Pin No(40, 38, 36)

# firebase certificate&init
cred = credentials.Certificate("/home/serviceAccountKey.json")
#'database url'
firebase_admin.initialize_app(cred,{'databaseURL' : 'https://test-energizor-default-rtdb.firebaseio.com'})

# new empty object
firebaseMacSet = set()
BLEScanMacSet = set()
matchedMacSet = set()
matchedMacList = []

while True:

    # clear
    firebaseMacSet.clear()
    BLEScanMacSet.clear()
    matchedMacSet.clear()
    matchedMacList.clear()

    # json-> object
    # Mac value List from.firebase 
    ref = db.reference().child("DataSet")
    snapshot = ref.get()
    for key in snapshot:
        firebaseResult = ref.child(key).child("Beacon").child("BeaconMAC")
        firebaseMacSet.add(firebaseResult.get())
        
    # BLEScan
    class ScanDelegate(DefaultDelegate):
        def __init__(self):
            DefaultDelegate.__init__(self)        
            def handleDiscovery(self, dev, isNewDev, isNewData):
                if isNewDev:
                    print("Discovered device %s "% dev.addr)
                elif isNewData:
                    print("Received new data from %s", dev.addr)
    scanner = Scanner().withDelegate(ScanDelegate())
    devices = scanner.scan(10.0) # setting scan time - 30s
    print("scan")

    # dev.addr = MAC
    # dev.addrType = if(public) -> fixed value
    for dev in devices:
        #trans capital
        devAddr = dev.addr
        BLEScanMacSet.add(devAddr.upper())

    # firebaseMAC match with BLEScanMaC
    matchedMacSet.update(firebaseMacSet & BLEScanMacSet)
    print(matchedMacSet)

    # LED on/off
    if len(matchedMacSet) > 0:
        LED.color = (1,0,1) #mazenta
        print("LED ON")
    else:
        LED.color = (0,0,0) #off
        print("LED OFF")

    # indexing, finding firebase
    matchedMacList = list(matchedMacSet)
    for idx in range(len(matchedMacList)):
        for key in snapshot:
            firebaseResult = ref.child(key).child("Beacon").child("BeaconMAC")
            if matchedMacList[idx] == firebaseResult.get():
                # send DeviceNo, location and time to firebase
                now = datetime.now() #current time
                ref.child(key).child("TrafficLight").child("Scan").update({"DeviceNo" : 58, "latitude": 36.1433, "longitude": 128.3933})
                ref.child(key).child("TrafficLight").child("Scan").child("Time").set(now.strftime('%Y-%m-%d %H:%M:%S'))
        
        #print("Device %s (%s), RSSI=%d dB" % (dev.addr, dev.addrType, dev.rssi))   
        #for(adtype, desc, value) in dev.getScanData():
            #print(" %s = %s" % (desc, value))
