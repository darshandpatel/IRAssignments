from PorterStemmer import PorterStemmer
import re
ps = PorterStemmer()
file_path = "/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/query_desc.51-100.short.txt"
stoplist_path = "/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/stoplist.txt"
query_terms = {}
stop_words=[]

with open(stoplist_path) as data_file:
    
    for line in data_file:
        line = re.sub('[\n]', '', line)
        stop_words.append(line)


with open(file_path) as data_file:
    
    for line in data_file:
        
        line = re.sub('[,.]', '', line)
        words = line.split()
        
        if len(words) >= 1:
            
            query_id = words[0]
            filtered_query_terms = []
            
            for index in range(4,len(words)):
                
                # if term is in stop word list do not consider it
                if words[index] in stop_words:
                    continue
                else:
                    # find the stemmed value of current term
                    stemmed_term = ps.stem(words[index],0,len(words[index]) - 1)
                    filtered_query_terms.append(stemmed_term)
            filtered_query_terms = list(set(filtered_query_terms))
            query_terms[query_id]=filtered_query_terms


for key, value in query_terms.iteritems():
    print key
    print value
                    
                