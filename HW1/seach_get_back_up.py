from elasticsearch import Elasticsearch
import operator
from PorterStemmer import PorterStemmer

es = Elasticsearch()
ps = PorterStemmer()

avg_doc_len = 247
print ps.stem("allegation",0,len("allegation") - 1)
'''


# ["allegation","corrupt","official","governmental","jurisdiction"]


final_tf = 0

word_doc_score_dic = {}
match_doc_ids_for_word = {}
#
query_words = ["allegation","corrupt","governmental","jurisdiction"]
for word in query_words:
    
    
    okapi_tf = 0
    doc_score_dic = {}
    res = es.search(
            index="documents",
            doc_type="stories",
            scroll='10s',
            search_type='scan',
            body={
                    "query": {
                        "match" : {
                            "text" : word
                        }
                    },
                    "script_fields": {
                        "terms" : {
                            "script": "doc[field].values",
                	        "params": {
                                    "field": "text"
                                    }
                            }
                    }
                }
            )
            
    
    #scroll_id = res['_scroll_id']

    total_doc_len = 0
    doc_ids = []
    match_doc_ids = []
    scroll_id = res['_scroll_id']
    scroll_size = res['hits']['total']
    print 'Total number of hits for word {} are {}'.format(word,scroll_size)
    while (scroll_size > 0):
        try:
            res = es.scroll(scroll_id=scroll_id, scroll='10s')
            for story in res['hits']['hits']:
                scroll_size -= 1
                match_doc_ids.append(story['_id'])
                doc_length = len(story['fields']['terms'][0])    
                
                rs = es.termvector(
                    index="documents",
                    doc_type="stories",
                    id=story["_id"],
                    term_statistics = "true"
                )
            
                term_freq = 0
                for term in rs["term_vectors"]["text"]["terms"]:
                    if term in word:
                        term_freq = rs["term_vectors"]["text"]["terms"][term]["ttf"]
                        break
                
                okapi_tf_w_d = (term_freq/(term_freq + 0.5 + ((1.5)*(doc_length/avg_doc_len))))
                doc_score_dic[story["_id"]]=okapi_tf_w_d
            scroll_id = res['_scroll_id']
        except: 
            break
    match_doc_ids_for_word[word] = match_doc_ids
    word_doc_score_dic[word] = doc_score_dic
    
    
merged_list = []
for doc_ids in match_doc_ids_for_word.keys():
    merged_list = merged_list + match_doc_ids_for_word[doc_ids]

final_doc_ids = list(set(merged_list))
final_doc_scores = {}
for doc_id in final_doc_ids:
    doc_score = 0.0
    for word in query_words:
        if doc_id in word_doc_score_dic[word]:
            doc_score += word_doc_score_dic[word][doc_id]
    final_doc_scores[doc_id]=doc_score

#sorted_doc_id = OrderedDict(sorted(final_doc_scores.items(), key=lambda t: t[1],reverse=True))
'''
'''
value = 50
for key, value in sorted(final_doc_scores.iteritems(), key=operator.itemgetter(1)):
	print key, ':', value
	value -= 1
	if (value == 0):
	    break
'''  
'''   
file = open("51.txt", "w")
rank = 1
for key, value in sorted(final_doc_scores.items(), key=operator.itemgetter(1),reverse=True):
    if rank <= 100:
        file.write("51 Q0 "+str(key)+" "+str(rank)+" "+str(value)+" Exp\n")
        rank += 1
file.close()


	
'''
    