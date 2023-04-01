import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import json
from bluepy.btle import Scanner, DefaultDelegate
from datetime import datetime

#firebase certificate&init
cred = credentials.Certificate("/home/serviceAccountKey.json")
#'database url'
firebase_admin.initialize_app(cred,{'databaseURL' : 'https://test-energizor-default-rtdb.firebaseio.com'})

# new empty object
firebaseMacSet = set()
BLEScanMacSet = set()
matchedMacSet = set()
matchedMacList = []

# json-> object
# Mac value List from.firebase 
ref = db.reference().child("DataSet")
snapshot = ref.get()
for key in snapshot:
    firebaseResult = ref.child(key).child("Beacon").child("MAC")
    firebaseMacSet.add(firebaseResult.get())

#print(firebaseMacList)

#send location to firebase
#ref.child("5001").child("TrafficLight").child("Scan").update({"Location": 109209})
    
class ScanDelegate(DefaultDelegate):
    def __init__(self):
        DefaultDelegate.__init__(self)
        
        def handleDiscovery(self, dev, isNewDev, isNewData):
            if isNewDev:
                print("Discovered device %s "% dev.addr)
            elif isNewData:
                print("Received new data from %s", dev.addr)
                
scanner = Scanner().withDelegate(ScanDelegate())
devices = scanner.scan(10.0)

#dev.addr = MAC
#dev.addrType = if(public) -> fixed value
for dev in devices:
    BLEScanMacSet.add(dev.addr)
    
#print(BLEScanMacList)

matchedMacSet.update(firebaseMacSet & BLEScanMacSet)
print(matchedMacSet)
matchedMacList = list(matchedMacSet)
for idx in range(len(matchedMacList)):
    for key in snapshot:
        firebaseResult = ref.child(key).child("Beacon").child("MAC")
        if matchedMacList[idx] == firebaseResult.get():
            #todo : LED on/off
            #send DeviceNo, location to firebase
            now = datetime.now()
            print(now)
            ref.child(key).child("TrafficLight").child("Scan").update({"DeviceNo" : 58, "Location": "109,209"})
            ref.child(key).child("TrafficLight").child("Scan").child("Time").set(now.strftime('%Y-%m-%d %H:%M:%S'))
    
    #, "Time" : now 
    #print("Device %s (%s), RSSI=%d dB" % (dev.addr, dev.addrType, dev.rssi))   
    #for(adtype, desc, value) in dev.getScanData():
        #print(" %s = %s" % (desc, value))
