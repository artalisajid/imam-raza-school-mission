#!/usr/bin/env python3
"""Generate original educational sticker PNGs with transparent backgrounds.
No sacred text, shrines, figures or religious architecture. Outputs are NOT public repo assets."""
from PIL import Image,ImageDraw,ImageFont,ImageFilter
from pathlib import Path
import json, math,zipfile
ROOT=Path(__file__).resolve().parents[1]
BASE=ROOT/"assets/paid-content"
F="/System/Library/Fonts/Supplemental/Arial Bold.ttf"
FONT="/System/Library/Fonts/Supplemental/Arial.ttf"
PACKS={
 "little_learners":["READ WITH JOY","KEEP LEARNING","HELLO BOOKS","TIME TO STUDY","BIG IDEAS","LITTLE SCHOLAR","ONE MORE PAGE","WRITE IT DOWN","ASK QUESTIONS","LET'S COUNT","BOOK BUDDY","YOU CAN DO IT"],
 "seeds_of_knowledge":["GROW EVERY DAY","PLANT A SEED","SMALL BEGINNING","GROW WITH CARE","NATURE LOVER","LEARN & BLOOM","FUTURE GROWS","STAY CURIOUS","GENTLE HEART","SHARE THE GOOD","BRIGHT MORNING","WATER & WONDER","NEW BEGINNINGS","KINDNESS GROWS","MORE SUNSHINE","GROW YOUR MIND","MAKE A DIFFERENCE","NATURE EXPLORER","BLOOM WITH JOY","A WORLD OF HOPE"],
 "bright_minds":["DISCOVER MORE","THINK BIG","SCIENCE CLUB","BRIGHT IDEAS","MATH MAGIC","CREATE DAILY","SOLVE IT","INVENT SOMETHING","SPACE EXPLORER","BE CURIOUS","CODE & CREATE","WHY NOT?","KEEP TRYING","EUREKA!","EXPLORE THE WORLD","NEW SOLUTIONS","IMAGINE MORE","ASK WHY","BE CREATIVE","BUILD YOUR FUTURE","THE LEARNING LAB","AHA MOMENT","DESIGN THE FUTURE","TRY AGAIN","KEEP EXPLORING","THINK DIFFERENTLY","MAKE IT WORK","IDEAS TAKE FLIGHT","START WITH WHY","KNOWLEDGE IS POWER"]
}
PALETTE={"little_learners":("#0c6047","#dfa64a","#f9edc9"),"seeds_of_knowledge":("#205d46","#85b87e","#f7f1db"),"bright_minds":("#15596c","#e3ad4c","#f5f1d9")}
def rgb(h):return tuple(bytes.fromhex(h[1:]))
def icon(draw,cx,cy,kind,primary,accent):
 p=rgb(primary);a=rgb(accent)
 if kind==0:
  draw.polygon([(cx,cy+80),(cx-150,cy+25),(cx-150,cy-85),(cx,cy-48)],fill="#fff9e8",outline=p,width=12)
  draw.polygon([(cx,cy+80),(cx+150,cy+25),(cx+150,cy-85),(cx,cy-48)],fill="#fff3cc",outline=p,width=12)
  draw.line([(cx,cy-42),(cx,cy+75)],fill=a,width=12)
 elif kind==1:
  draw.line([(cx,cy+92),(cx,cy-70)],fill=p,width=18)
  draw.ellipse((cx-138,cy-100,cx,cy-22),fill=a,outline=p,width=9)
  draw.ellipse((cx+6,cy-132,cx+133,cy-48),fill="#64a981",outline=p,width=9)
  draw.ellipse((cx-67,cy-170,cx+55,cy-50),fill="#81b58d",outline=p,width=9)
 elif kind==2:
  draw.polygon([(cx-98,cy+82),(cx-34,cy+109),(cx+106,cy-117),(cx+38,cy-147)],fill=a,outline=p,width=12)
  draw.polygon([(cx-98,cy+82),(cx-34,cy+109),(cx-93,cy+143)],fill="#e6bb8b",outline=p,width=7)
  draw.line([(cx+38,cy-146),(cx+107,cy-116)],fill=p,width=11)
 elif kind==3:
  draw.ellipse((cx-96,cy-146,cx+96,cy+48),fill="#f6d98e",outline=p,width=12)
  draw.line([(cx-65,cy+55),(cx+65,cy+55)],fill=p,width=16)
  draw.rounded_rectangle((cx-55,cy+78,cx+55,cy+119),radius=14,fill=a,outline=p,width=8)
 elif kind==4:
  draw.ellipse((cx-112,cy-132,cx+112,cy+93),fill="#88ccd0",outline=p,width=12)
  draw.arc((cx-60,cy-130,cx+60,cy+92),70,290,fill=p,width=12)
  draw.line([(cx-95,cy-13),(cx+95,cy-13)],fill=p,width=11)
  draw.arc((cx-155,cy-175,cx+155,cy+134),35,149,fill=a,width=12)
 elif kind==5:
  for x,y,r in [(cx,cy-30,115),(cx-135,cy+45,40),(cx+130,cy-99,55)]:
   pts=[]
   for k in range(10):
    th=k*math.pi/5-math.pi/2;v=r if k%2==0 else r*.41
    pts.append((x+math.cos(th)*v,y+math.sin(th)*v))
   draw.polygon(pts,fill=a if r==115 else "#86b59b",outline=p,width=9)
 elif kind==6:
  draw.rounded_rectangle((cx-135,cy-70,cx+135,cy+80),radius=28,fill="#e3b86c",outline=p,width=12)
  for n in range(1,6):
   draw.line([(cx-114+n*42,cy-70),(cx-114+n*42,cy-24 if n%2==0 else cy-5)],fill=p,width=9)
 elif kind==7:
  draw.ellipse((cx-125,cy-128,cx+125,cy+122),outline=p,width=10)
  draw.ellipse((cx-115,cy-46,cx+116,cy+39),outline=a,width=12)
  draw.ellipse((cx-55,cy-125,cx+55,cy+125),outline=p,width=11)
  draw.ellipse((cx-20,cy-20,cx+20,cy+20),fill=a)
 else:
  draw.polygon([(cx-140,cy-40),(cx,cy+100),(cx+140,cy-40)],fill="#f3d59b",outline=p,width=12)
  draw.ellipse((cx-88,cy-141,cx+88,cy+45),outline=p,width=14)
  draw.ellipse((cx-32,cy-86,cx+32,cy-22),fill=a)
def one(text,kind,primary,accent,cream):
 img=Image.new("RGBA",(1024,1024),(0,0,0,0))
 shadow=Image.new("RGBA",img.size,(0,0,0,0));sd=ImageDraw.Draw(shadow)
 sd.rounded_rectangle((54,65,965,971),radius=230,fill=(0,0,0,95))
 shadow=shadow.filter(ImageFilter.GaussianBlur(24));img.alpha_composite(shadow)
 d=ImageDraw.Draw(img)
 d.rounded_rectangle((43,35,962,957),radius=230,fill=(255,255,251,255),outline=(230,230,215),width=26)
 d.rounded_rectangle((78,70,927,922),radius=207,fill=rgb(cream),outline=rgb(primary),width=16)
 d.arc((137,112,876,874),205,335,fill=rgb(accent),width=10)
 d.ellipse((425,110,605,289),fill=rgb(accent),outline=rgb(primary),width=10)
 d.text((515,202),"✦",font=ImageFont.truetype(FONT,89),fill=rgb(primary),anchor="mm")
 icon(d,512,505,kind,primary,accent)
 ft=ImageFont.truetype(F,52 if len(text)<17 else 43)
 bbox=d.textbbox((0,0),text,font=ft)
 if bbox[2]-bbox[0]>680:ft=ImageFont.truetype(F,36)
 d.rounded_rectangle((147,722,877,851),radius=55,fill=rgb(primary))
 d.text((512,785),text,font=ft,fill="#fff9ec",anchor="mm",stroke_width=0)
 return img.resize((512,512),Image.Resampling.LANCZOS)
for pack,phrases in PACKS.items():
 path=BASE/pack;path.mkdir(parents=True,exist_ok=True)
 primary,accent,cream=PALETTE[pack]
 for ix,text in enumerate(phrases):
  image=one(text,ix%9,primary,accent,cream)
  image.save(path/f"{ix+1:02d}_{text.lower().replace(' ','_').replace('&','and').replace('?','').replace('!','')}.png")
 licensefile=path/"PERSONAL_USE_LICENSE.txt"
 licensefile.write_text("Imam Raza (a.s.) School Mission\nOriginal digital sticker illustrations.\nPersonal, non-commercial use and sharing permitted. Redistribution or resale as a collection is not permitted.\nThese are digital art products, not charitable donation receipts.\n")
 archive=BASE/(pack+".zip")
 with zipfile.ZipFile(archive,"w",zipfile.ZIP_DEFLATED,compresslevel=8) as z:
  for file in sorted(path.iterdir()):z.write(file,file.name)
 print(pack,len(phrases),"stickers",round(archive.stat().st_size/1024),"KB",archive)
