from elasticsearch import Elasticsearch
import sys
es = Elasticsearch()

res = es.search(
        index="documents",
        doc_type="stories",
        scroll='20s',
        search_type='scan',
        size = 20,
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
while (scroll_size > 0):
    try:
        res = es.scroll(scroll_id=scroll_id, scroll='20s')

        story_ids = []
        for story in res['hits']['hits']:
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
        for doc in rs["docs"]:
            if len(doc["term_vectors"]) != 0 :          
                for term in doc["term_vectors"]["text"]["terms"]:
                    total_doc_len += doc["term_vectors"]["text"]["terms"][term]["term_freq"]
    except: 
        print "This is serious condition !! Resolve it as soon as possible"
        print story_ids
        print "Unexpected error:", sys.exc_info()[0]
        break
avg_doc_len =  total_doc_len / total_no_of_doc
print "Average Document length is : {}".format(avg_doc_len)
print "value of scroll size is : {}".format(total_no_of_doc)
