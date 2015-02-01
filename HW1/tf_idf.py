from elasticsearch import Elasticsearch
from PorterStemmer import PorterStemmer
import re
import math

#-------------------------------------------------------------------------------
docs_length = {}
total_doc_length = 0
query_outputs={}
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
avg_doc_len = total_doc_length / float(no_of_doc)
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
        
        line = re.sub('[,."()\n]', '', line)
        words = line.split()
        
        if len(words) >= 1:

            query_id = words[0]
            filtered_query_terms = []
            
            for index in range(4,len(words)):
                
                qterm = words[index].lower()
                # if term is in stop word list do not consider it
                if qterm in stop_words:
                    continue
                else:
                    # find the stemmed value of current term
                    stemmed_term = ps.stem(qterm,0,len(qterm) - 1)
                    filtered_query_terms.append(stemmed_term)
            filtered_query_terms = list(set(filtered_query_terms))
            query_terms[query_id]=filtered_query_terms

#-------------------------------------------------------------------------------
doc_score_per_term = {}
match_doc_ids_per_term = {}
doc_freq_per_term={}
for no, terms in query_terms.iteritems():

    for term in terms:
    
        res = es.search(
                index="documents",
                doc_type="stories",
                size=no_of_doc,
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
                        "size": no_of_doc,
                        "fields": ["stream_id"]
                    }
                )
            
    
        match_doc_ids = []
        doc_score_dic = {}
        total_hits = res['hits']['total']
        doc_freq_per_term[term]=total_hits
        print 'Total number of hits for term {} are {}'.format(term,total_hits)
        for story in res['hits']['hits']:
            match_doc_ids.append(story['_id'])
            doc_length = docs_length[story['_id']]                    
            term_freq = story['_score']
            okapi_tf_w_d = (float(term_freq)/(term_freq + 0.5 + ((1.5)*(float(doc_length)/avg_doc_len))))
            doc_score_dic[story["_id"]]=okapi_tf_w_d

                
        match_doc_ids_per_term[term] = match_doc_ids
        doc_score_per_term[term] = doc_score_dic
    
    
    merged_list = []
    for term in match_doc_ids_per_term.keys():
        merged_list = merged_list + match_doc_ids_per_term[term]
    
    final_doc_ids = list(set(merged_list))
    doc_tf_idf_score = {}
    
    for doc_id in final_doc_ids:
        score = 0.0
        for term in terms:
            if doc_id in doc_score_per_term[term]:
                score += (doc_score_per_term[term][doc_id] * (math.log(no_of_doc/doc_freq_per_term[term])))
        doc_tf_idf_score[doc_id]=round(score,2)

    
    #sorted_doc = OrderedDict(sorted(doc_okapi_tf_score.items(), key=lambda t: t[1],reverse=True))
    sorted_doc = sorted(doc_tf_idf_score.iteritems(), key=lambda x:-x[1])[:1000]
    query_outputs[no] = sorted_doc
#query_outputs = collections.OrderedDict(sorted(query_outputs.items()))
file = open("/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/tfidfoutput.txt", "w")
for no, sorted_doc in query_outputs.iteritems():
    rank = 1
    for key, value in sorted_doc:
        string = "{} Q0 {} {} {} Exp\n".format(no,key,rank,value)
        file.write(string)
        rank += 1
file.close()
