from elasticsearch import Elasticsearch
es = Elasticsearch()


res = es.search(
        index="documents",
        doc_type="stories",
        scroll='10s',
        search_type='scan',
        body={
                "query" : {
                    "match_all" : {}
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

total_doc_len = 0

stories = []
scroll_id = res['_scroll_id']
scroll_size = res['hits']['total']
while (scroll_size > 0):
    try:
        res = es.scroll(scroll_id=scroll_id, scroll='10s')
        for story in res['hits']['hits']:
            total_doc_len += len(story['fields']['terms'][0])  
    except: 
        break
avg_doc_len =  total_doc_len / scroll_size
print "Average Document length is : {}".format(avg_doc_len)  
#print "After a long while loop value of count is {}".format(count)

#total_doc_len = 0
#print 'Total found record after scroll is {}'.format(len(stories))
'''
for story in stories:
    try:
        rs = es.termvector(
            index="documents",
            doc_type="stories",
            id=story["_id"],
            term_statistics = "true"
        )
        term_count = 0
        for term in rs["term_vectors"]["text"]["terms"]:
            term_count += 1
        total_doc_len += term_count;
    except:
        continue
    
avg_doc_len =  total_doc_len / scroll_size
print "Average Document length is : {}".format(avg_doc_len)  
'''  
# ["allegation","corrupt","official","governmental","jurisdiction"]


final_tf = 0

word_doc_score_dic = {}
for word in ["allegation","corrupt","official","governmental","jurisdiction"]:
    
    
    okapi_tf = 0
    doc_score_dic = {}
    res = es.search(
            index="documents",
            doc_type="stories",
            analyzer = "my_english",
            analyze_wildcard = "true",
            scroll='10s',
            search_type='scan',
            size=0,
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

    scroll_id = res['_scroll_id']
    scroll_size = res['hits']['total']
    print 'Total number of hits are {}'.format(scroll_size)
    while (scroll_size > 0):
        try:
            res = es.scroll(scroll_id=scroll_id, scroll='10s')
            for story in res['hits']['hits']:
                doc_length = len(story['fields']['terms'][0])
                # Solve the equation here
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
                
        except: 
            break
        
    word_doc_score_dic[word]= doc_score_dic

    



'''
    while (scroll_size > 0):
        try:
            res = es.scroll(scroll_id=scroll_id, scroll='10s')
            tweets += res['hits']['hits']
            
            for story in res['hits']['hits'][0]:
                rs = es.termvector(
                    index="documents",
   	            doc_type="stories",
   	            id=story["_id"],
                    field_statistics = "true",
                    term_statistics = "true"
                )
        
            print (rs["_id"])
            print (rs["term_vectors"]["text"]["terms"]["alleg"]["term_freq"])
            count+=1
            
            scroll_id = res['_scroll_id']
            scroll_size = res['hits']['total']
        except: 
            break
'''
'''
    for story in tweets:
    
        res = es.termvector(
            index="documents",
	    doc_type="stories",
	    id=story["_id"],
            field_statistics = "true",
            term_statistics = "true"
            )
        
        print (res["_id"])
        print (res["term_vectors"]["text"]["terms"]["alleg"]["term_freq"])
'''