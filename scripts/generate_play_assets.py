#!/usr/bin/env python3
"""Imam Raza School Mission – Google Play assets.
Original non-sacred academic/book/sprig theme. Product stickers are our own designs.
App icon 512 RGBA; Feature 1024x500 24-bit RGB; screenshot footage comes from the real app.
"""
from pathlib import Path
from PIL import Image,ImageDraw,ImageFont,ImageFilter
import math,random
ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/"play-store-assets"
OUT.mkdir(exist_ok=True)
S=3
G="#0a4938"; G2="#063627"; IV="#fff9e8"; GOLD="#dfaf62"; MID="#8db99f"
SERIF="/System/Library/Fonts/Supplemental/Georgia Bold.ttf"
SANS="/System/Library/Fonts/Supplemental/Arial Bold.ttf"
def font(path,size):return ImageFont.truetype(path,size*S)
def gradient(w,h,top,bottom):
 from PIL import ImageColor
 a=ImageColor.getrgb(top);b=ImageColor.getrgb(bottom)
 im=Image.new('RGB',(w,h)); pix=im.load()
 for y in range(h):
  t=y/max(1,h-1)
  v=tuple(int(a[i]*(1-t)+b[i]*t) for i in range(3))
  for x in range(w):pix[x,y]=v
 return im
def book(draw,x,y,size,stroke):
 # Ivory open-page spread with a central gold accent.
 r=size/2
 draw.polygon([(x,y+r*.56),(x-r*.89,y+r*.26),(x-r*.89,y-r*.56),
               (x,y-r*.25)],fill=IV,outline=stroke,width=13*S)
 draw.polygon([(x,y+r*.56),(x+r*.89,y+r*.26),(x+r*.89,y-r*.56),
               (x,y-r*.25)],fill="#f3e1b6",outline=stroke,width=13*S)
 draw.line([(x,y-r*.22),(x,y+r*.50)],fill=G,width=11*S)
 for z in (-1,1):
  for k in range(2):
   yy=y-r*(.27-k*.22)
   draw.line([(x+z*r*.17,yy),(x+z*r*.69,yy-r*.11)],fill="#bdbe9e",width=4*S)
def sprig(draw,x,y,size):
 draw.line([(x,y+size*.42),(x,y-size*.38)],fill=G,width=15*S)
 leaves=[
  (x-size*.10,y-size*.20,x-size*.55,y-size*.52),
  (x+size*.10,y-size*.28,x+size*.49,y-size*.64),
  (x-size*.01,y-size*.47,x-size*.19,y-size*.91),
 ]
 for x1,y1,x2,y2 in leaves:
  lo=(min(x1,x2),min(y1,y2),max(x1,x2),max(y1,y2))
  draw.ellipse(lo,fill=G,outline=G2,width=3*S)
# Play Store icon: full square; Google applies masking, no pre-rounded corners.
icon=gradient(512*S,512*S,G,G2).convert('RGBA')
d=ImageDraw.Draw(icon)
for j in range(14):
 a=j*math.pi/7
 d.ellipse((int((256+211*math.cos(a))*S),int((256+211*math.sin(a))*S),
            int((256+211*math.cos(a))*S+6*S),int((256+211*math.sin(a))*S+6*S)),fill=GOLD)
d.ellipse((55*S,55*S,457*S,457*S),outline=GOLD,width=6*S)
d.ellipse((69*S,69*S,443*S,443*S),outline=(245,223,171,90),width=2*S)
book(d,256*S,298*S,260*S,G2)
sprig(d,255*S,230*S,156*S)
icon=icon.resize((512,512),Image.Resampling.LANCZOS)
icon.save(OUT/"play_icon_512.png",optimize=True)
# Feature graphic has central wordmark and authentic sticker previews
w,h=1024,500
img=gradient(w*S,h*S,"#fff6de","#efe5ca").convert('RGB')
d=ImageDraw.Draw(img)
d.ellipse((-260*S,-220*S,750*S,810*S),outline=GOLD,width=4*S)
d.ellipse((-120*S,-130*S,655*S,665*S),outline="#e4c793",width=2*S)
d.polygon([(0,390*S),(0,500*S),(w*S,500*S),(w*S,420*S),(760*S,440*S),(490*S,405*S)],fill=G2)
d.polygon([(680*S,0),(1024*S,0),(1024*S,500*S),(715*S,500*S),
           (665*S,340*S),(715*S,212*S)],fill=G)
# Tiny geometry flecks and botanical edges
rand=random.Random(17)
for _ in range(45):
 x=rand.randrange(760,1020)*S;y=rand.randrange(20,490)*S
 size=rand.randrange(3,8)*S
 d.ellipse((x-size,y-size,x+size,y+size),fill=GOLD if rand.randrange(4)==0 else MID)
# Wordmark left
d.text((74*S,82*S),"IMAM RAZA (a.s.)",font=font(SERIF,39),fill=G)
d.text((72*S,143*S),"SCHOOL MISSION",font=font(SERIF,39),fill=G)
d.line((74*S,223*S,612*S,223*S),fill=GOLD,width=4*S)
d.text((74*S,250*S),"Books. Knowledge. Opportunity.",font=font(SANS,19),fill="#3b705b")
d.text((74*S,300*S),"Discover educational sticker art",font=font(SANS,17),fill=G2)
d.text((74*S,330*S),"and follow a proposed school journey.",font=font(SANS,17),fill=G2)
# Sticker illustration collage on right; no depiction of holy people or shrines.
previews=[]
candidates=[
 ROOT/"assets/paid-content"/"little_learners"/"01_read_with_joy.png",
 ROOT/"assets/paid-content"/"seeds_of_knowledge"/"01_grow_every_day.png",
 ROOT/"assets/paid-content"/"bright_minds"/"01_discover_more.png",
]
for n,p in enumerate(candidates):
 if p.exists():
  tile=Image.open(p).convert("RGBA").resize((207*S,207*S),Image.Resampling.LANCZOS)
  tile=tile.rotate([14,-13,9][n],expand=True,resample=Image.Resampling.BICUBIC)
  previews.append((tile,[(721,95),(820,224),(850,8)][n]))
canvas=img.convert('RGBA')
for tile,(x,y) in previews:
 shadow=Image.new("RGBA",tile.size,(0,0,0,0))
 shadow.putalpha(tile.getchannel("A").filter(ImageFilter.GaussianBlur(18*S)))
 canvas.alpha_composite(shadow,(x*S+9*S,y*S+11*S))
 canvas.alpha_composite(tile,(x*S,y*S))
canvas.convert("RGB").resize((1024,500),Image.Resampling.LANCZOS).save(
 OUT/"feature_graphic_1024x500.png",optimize=True)
print("ICON",OUT/"play_icon_512.png", (OUT/"play_icon_512.png").stat().st_size)
print("FEATURE",OUT/"feature_graphic_1024x500.png",(OUT/"feature_graphic_1024x500.png").stat().st_size)
