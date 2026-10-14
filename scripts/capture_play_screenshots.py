#!/usr/bin/env python3
"""Capture real Android UI screenshots from connected Pixel via ADB.
Temporarily overrides rendering to 1080x1920 for Play's recommended 9:16.
Restores device configuration in all cases. Redacts system notification bar.
"""
from pathlib import Path
from PIL import Image,ImageDraw
from io import BytesIO
import subprocess,xml.etree.ElementTree as ET,time
ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/"play-store-assets"/"phone_screenshots"
OUT.mkdir(parents=True,exist_ok=True)
ADB="/opt/homebrew/share/android-commandlinetools/platform-tools/adb"
def adb(*args,timeout=20):return subprocess.run([ADB,*args],capture_output=True,timeout=timeout,check=True).stdout
def screenshot(name):
 data=adb("exec-out","screencap","-p")
 image=Image.open(BytesIO(data)).convert("RGB")
 if image.size != (1080,1920):
  raise RuntimeError(f"Unexpected device size {image.size}, not 1080x1920")
 # Hide status notifications belonging to the owner's real phone; preserve app UI.
 ImageDraw.Draw(image).rectangle((0,0,1080,76),fill="#0a4938")
 path=OUT/name
 image.save(path,optimize=True)
 print("CAPTURED",path.name,image.size,flush=True)
def nodes():
 adb("shell","uiautomator","dump","/sdcard/play_qa.xml",timeout=35)
 raw=adb("exec-out","cat","/sdcard/play_qa.xml")
 return list(ET.fromstring(raw).iter())
def tap(text,which=0):
 found=[n for n in nodes() if (n.attrib.get("text") or "").lower()==text.lower()]
 if not found:
  print("NOT_FOUND",text,flush=True);return False
 n=found[which if which<len(found) else -1]
 b=n.attrib["bounds"]
 import re
 a=list(map(int,re.findall(r"\d+",b)))
 x=(a[0]+a[2])//2;y=(a[1]+a[3])//2
 adb("shell","input","tap",str(x),str(y))
 time.sleep(.65)
 return True
def main():
 device=adb("shell","wm","size").decode()
 print("Original dimensions:",device.strip(),flush=True)
 adb("shell","wm","size","1080x1920")
 adb("shell","wm","density","360")
 original_alerts=adb("shell","settings","get","global","heads_up_notifications_enabled").decode().strip()
 adb("shell","settings","put","global","heads_up_notifications_enabled","0")
 try:
  adb("shell","am","force-stop","org.imamraza.schoolmission")
  adb("shell","am","start","-n","org.imamraza.schoolmission/.MainActivity")
  time.sleep(3.2)
  tap("Account")
  tap("Sign out")
  tap("Home")
  time.sleep(1.5)
  screenshot("01_home.png")
  if not tap("Support"):
   time.sleep(1.5)
   assert tap("Support"),"Could not select Support tab"
  screenshot("02_support.png")
  adb("shell","input","swipe","530","1510","530","880","520")
  time.sleep(.6)
  screenshot("03_sticker_collections.png")
  # Captured current real catalog, then open the first authentic sticker product.
  textlist=[x.attrib.get("text") for x in nodes() if x.attrib.get("text")]
  print("SUPPORT_TITLES",textlist[:30],flush=True)
  if tap("Little Learners"):
   screenshot("03_sticker_detail.png")
   adb("shell","input","keyevent","KEYCODE_BACK")
  tap("School");screenshot("04_school.png")
  tap("Journey");screenshot("05_journey.png")
  tap("Account");screenshot("06_account_guest.png")
  print("CAPTURE_COMPLETE",len(list(OUT.glob("*.png"))),flush=True)
 finally:
  if original_alerts=="null":adb("shell","settings","delete","global","heads_up_notifications_enabled")
  else:adb("shell","settings","put","global","heads_up_notifications_enabled",original_alerts)
  adb("shell","wm","density","reset")
  adb("shell","wm","size","reset")
  print("DEVICE_SIZE_RESTORED",adb("shell","wm","size").decode().strip(),flush=True)
if __name__=="__main__":main()
