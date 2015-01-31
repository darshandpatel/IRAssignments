from elasticsearch import Elasticsearch
from PorterStemmer import PorterStemmer
import re
import operator

#-------------------------------------------------------------------------------
docs_length = {}
total_doc_length = 0
no_of_doc = 0
avg_doc_len = 0.0
doclengths_path = "/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/doclengths.txt"
es = Elasticsearch()

with open(doclengths_path) as data_file:
    
    for line in data_file:
        words = line.split()
        docs_length[words[0]] = int(words[1])
        total_doc_length += int(words[1])
        no_of_doc += 1
avg_doc_len = total_doc_length / no_of_doc
print "Average document length is {}".format(avg_doc_len)

#-------------------------------------------------------------------------------
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

doc_score_per_term = {}
match_doc_ids_per_term = {}

#-------------------------------------------------------------------------------

for no, terms in query_terms.iteritems():

    for term in terms:
    
        res = es.search(
                index="documents",
                doc_type="stories",
                scroll='10s',
                search_type='scan',
                body={
                        "query": {
                            "function_score": {
                                "query": {
                                    "match": {
                                        "text": term
                                    }
                            },
                            "functions": [
                                {
                                    "script_score": {
                                        "script_id": "calTF",
                                        "lang" : "groovy",
                                        "params": {
                                            "term": term,
                                            "field": "text"
                                        }
                                    }
                                }
                            ],
                            "boost_mode": "replace"
                            }
                        },
                        "size": 10000,
                        "fields": ["stream_id"]
                    }
                )
            
    
        match_doc_ids = []
        doc_score_dic = {}
        scroll_id = res['_scroll_id']
        scroll_size = res['hits']['total']
        print 'Total number of hits for term {} are {}'.format(term,scroll_size)
        while (scroll_size > 0):
            try:
                res = es.scroll(scroll_id=scroll_id, scroll='10s')
                for story in res['hits']['hits']:
                    scroll_size -= 1
                    match_doc_ids.append(story['_id'])
                    doc_length = docs_length[story['_id']]                    
                    term_freq = story['_score']
                    print story
                    okapi_tf_w_d = (term_freq/(term_freq + 0.5 + ((1.5)*(doc_length/avg_doc_len))))
                    doc_score_dic[story["_id"]]=okapi_tf_w_d
                    
                scroll_id = res['_scroll_id']
            except: 
                break
                
        match_doc_ids_per_term[term] = match_doc_ids
        doc_score_per_term[term] = doc_score_dic
    
    
    merged_list = []
    for term in match_doc_ids_per_term.keys():
        merged_list = merged_list + match_doc_ids_per_term[term]
    
    final_doc_ids = list(set(merged_list))
    doc_okapi_tf_score = {}
    
    for doc_id in final_doc_ids:
        score = 0.0
        for term in terms:
            if doc_id in doc_score_per_term[term]:
                score += doc_score_per_term[term][doc_id]
        doc_okapi_tf_score[doc_id]=score
    
    #sorted_doc = OrderedDict(sorted(doc_okapi_tf_score.items(), key=lambda t: t[1],reverse=True))
    sorted_doc = sorted(doc_okapi_tf_score.iteritems(), key=lambda x:-x[1])[:100]
    
  
    file = open(no+".txt", "w")
    rank = 1
    for key, value in sorted_doc:
            print key,value
            file.write(no+" Q0 "+str(key)+" "+str(rank)+" "+str(value)+" Exp\n")
            rank += 1
    file.close()
    
    break
    