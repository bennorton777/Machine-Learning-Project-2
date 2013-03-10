import math
FILE="data5000.txt"



infile=open(FILE, "r")
rhcfile=open(FILE+"rhc","w")
safile=open(FILE+"sa","w")
gafile=open(FILE+"ga","w")
line = infile.readline()
mode="ga"
rhcString=''
saString=''
gaString=''
while (line!=''):
	if line[0:7]=="Results":
		if mode=="rhc":
			mode="sa"
		elif mode=="sa":
			mode="ga"
		elif mode=="ga":
			mode="rhc"
	elif line[0:5]=="Error" or line[0]=="-":
		#do nothing
		count=0
	else:
		if mode=="rhc":
			rhcfile.write(line)
		if mode=="sa":
			safile.write(line)
		if mode=="ga":
			gafile.write(line)


	line=infile.readline()
infile.close()
rhcfile.close()
safile.close()
gafile.close()

rhcfile=open(FILE+"rhc","r")
safile=open(FILE+"sa","r")
gafile=open(FILE+"ga","r")

stats={'rhcStats':{},'saStats':{},'gaStats':{}}
for key in stats.keys():
	stats[key]['elements']=[]
	stats[key]['times']=[]
line = rhcfile.readline()
while (line!=''):
	if line=='\n':
		line=rhcfile.readline()
		continue
	if line[-2]=="%":
		print 'rhc stat found'
		stats['rhcStats']['elements']+=[float(line[-8:-2])]
	if line[0:8]=="Training":
		print 'rhc time found'
		stats['rhcStats']['times']+=[float(line.split()[2])]

	line=rhcfile.readline()

line = safile.readline()
while (line!=''):
	if line=='\n':
		line=safile.readline()
		continue
	if line[-2]=="%":
		print 'sa stat found'	
		stats['saStats']['elements']+=[float(line[-8:-2])]
	if line[0:8]=="Training":
		print 'sa time found'
		stats['saStats']['times']+=[float(line.split()[2])]
	line=safile.readline()


line = gafile.readline()
while (line!=''):
	if line=='\n':
		line=gafile.readline()
		continue
	if line[-2]=="%":
		print 'ga stat found'
		stats['gaStats']['elements']+=[float(line[-8:-2])]	
	if line[0:8]=="Training":
		print 'ga time found'
		stats['gaStats']['times']+=[float(line.split()[2])]
	line=gafile.readline()

for key in stats.keys():
	mean=0
	count=0
	timeMean=0
	stats[key]['devs']=[]
	for element in stats[key]['elements']:
		count+=1
		mean+=element
	for time in stats[key]['times']:
		timeMean+=time
	stats[key]['timeMean']=timeMean/count
	mean=mean/count
	stats[key]['mean']=mean
	stats[key]['count']=count
	for i in range(0,len(stats[key]['elements'])):
		stats[key]['devs']+=[stats[key]['elements'][i]-mean]
	sumSquares=0
	for dev in stats[key]['devs']:
		sumSquares+=dev*dev
	stats[key]['standardDeviation']=math.sqrt(sumSquares/(count-1))
rhcfile.close()
safile.close()
gafile.close()
rhcfile=open(FILE+"rhc","a")
safile=open(FILE+"sa","a")
gafile=open(FILE+"ga","a")

rhcfile.write("Average is "+str(stats['rhcStats']['mean'])+" and standard Deviation is "+str(stats['rhcStats']['standardDeviation'])+" in "+str(stats['rhcStats']['timeMean'])+" over "+str(stats['rhcStats']['count'])+" runs")
safile.write("Average is "+str(stats['saStats']['mean'])+" and standard Deviation is "+str(stats['saStats']['standardDeviation'])+" in "+str(stats['saStats']['timeMean'])+" over "+str(stats['saStats']['count'])+" runs")
gafile.write("Average is "+str(stats['gaStats']['mean'])+" and standard Deviation is "+str(stats['gaStats']['standardDeviation'])+" in "+str(stats['gaStats']['timeMean'])+" over "+str(stats['gaStats']['count'])+" runs")
rhcfile.close()
safile.close()
gafile.close()
