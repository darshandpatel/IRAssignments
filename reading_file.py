import glob
#print glob.glob("/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/ap89_collection/*")

for file_path in glob.glob("/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/ap89_collection/*"):
   if 'readme' in file_path:
        with open(file_path) as data_file:
            for line in data_file:
                print line