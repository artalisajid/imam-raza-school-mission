from PIL import Image,ImageDraw,ImageFont
from pathlib import Path
root=Path(__file__).resolve().parents[1]
out=root/"app/src/main/res/drawable-nodpi"
out.mkdir(parents=True,exist_ok=True)
colors=[(224,237,223),(225,236,221),(218,233,237)]
names=["LITTLE LEARNERS","SEEDS OF KNOWLEDGE","BRIGHT MINDS"]
files=["preview_little_learners.png","preview_seeds_of_knowledge.png","preview_bright_minds.png"]
for i in range(3):
 im=Image.new("RGB",(1000,700),colors[i]);d=ImageDraw.Draw(im)
 for y in range(700):
  t=y/700
  d.line((0,y,1000,y),fill=tuple(int(colors[i][k]*(1-t)+(255,246,224)[k]*t) for k in range(3)))
 d.rounded_rectangle((70,60,930,640),radius=68,fill=(250,244,224),outline=(199,156,78),width=11)
 for x,y,r in [(170,150,32),(826,155,26),(195,515,22),(805,535,30)]:
  d.ellipse((x-r,y-r,x+r,y+r),fill=(225,193,121))
 d.polygon([(490,285),(235,245),(235,455),(490,510)],fill=(255,255,241),outline=(198,152,90),width=7)
 d.polygon([(510,285),(765,245),(765,455),(510,510)],fill=(255,250,223),outline=(198,152,90),width=7)
 d.line((500,290,500,510),fill=(176,127,59),width=13)
 for n in range(4):
  d.line((290,292+n*35,450,330+n*34),fill=(215,194,143),width=7)
  d.line((545,330+n*34,702,292+n*35),fill=(215,194,143),width=7)
 d.line((500,290,500,115),fill=(19,114,76),width=19)
 d.ellipse((405,137,510,201),fill=(46,142,86))
 d.ellipse((500,105,619,166),fill=(37,122,79))
 d.ellipse((457,70,542,156),fill=(76,163,99))
 d.ellipse((285,440,360,512),fill=(60,146,96))
 d.ellipse((670,455,745,527),fill=(86,155,110))
 f=ImageFont.truetype("/System/Library/Fonts/Supplemental/Georgia.ttf",39)
 d.text((500,550),names[i],font=f,anchor="mm",fill=(6,68,52))
 im.save(out/files[i],optimize=True)
 print(out/files[i])
