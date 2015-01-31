from elasticsearch import Elasticsearch
import sys
es = Elasticsearch()

res = es.search(
        index="documents",
        doc_type="stories",
        scroll='5s',
        search_type='scan',
        body={
                "query" : {
                    "match_all" : {}
                }
            }
        )

total_doc_len = 0
total_no_of_doc = res['hits']['total']
scroll_id = res['_scroll_id']
scroll_size = total_no_of_doc
count = 0
error_ids =['AP890630-0240', 'AP890630-0241', 'AP890630-0242', 'AP890630-0243', 'AP890630-0244', 
'AP890630-0245', 'AP890630-0246', 'AP890630-0247', 'AP890630-0248', 'AP890630-0249']
flag = False
while (scroll_size > 0):
    try:
        res = es.scroll(scroll_id=scroll_id, scroll='5s')

        
        story_ids = []
        for story in res['hits']['hits']:
            if story["_id"] in error_ids:
                print "Keep watch 1"
                flag = True
            scroll_size -= 1
            story_ids.append(story["_id"])
        
        scroll_id = res['_scroll_id']    
        rs = es.mtermvectors(
            index="documents",
            doc_type="stories",
            ids=story_ids,
            field_statistics = "false",
            offsets="false",
            payloads="false",
            positions="false"
        )
        if flag == True:
            print "Keep watch 2"
        for doc in rs["docs"]: 
            count += 1
            if flag == True:
                print "Keep watch 3"
                print doc["_id"]          
            for term in doc["term_vectors"]["text"]["terms"]:
                total_doc_len += doc["term_vectors"]["text"]["terms"][term]["term_freq"]
            if doc["_id"] == "AP890630-0247":
                print "Clean cit to AP890630-0247"
        if flag == True:
            print "Keep watch 3"
            print story_ids
    except: 
        print "This is serious condition !! Resolve it as soon as possible"
        print story_ids
        print "Unexpected error:", sys.exc_info()[0]
        break
avg_doc_len =  total_doc_len / count
print "Average Document length is : {}".format(avg_doc_len)
print "value of count is : {}".format(count)
print "value of scroll size is : {}".format(scroll_size)
