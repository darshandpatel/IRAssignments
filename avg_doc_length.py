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
                }
            }
        )

total_doc_len = 0
total_no_of_doc = res['hits']['total']
scroll_id = res['_scroll_id']
scroll_size = res['hits']['total']


while (scroll_size > 0):
    try:
        res = es.scroll(scroll_id=scroll_id, scroll='10s')
        for story in res['hits']['hits']:
            
            print "Doc Id {}".format(story["_id"]),
            rs = es.termvector(
                index="documents",
                doc_type="stories",
                id=story["_id"],
                term_statistics = "true"
            )
            
            doc_length = 0
            print rs
            break
            for term in rs["term_vectors"]["text"]["terms"]:
                doc_length += rs["term_vectors"]["text"]["terms"][term]["ttf"]
            print "Doc Length {}".format(doc_length)
            total_doc_len += doc_length
            scroll_size -= 1
            
        scroll_id = res['_scroll_id']
        break
    except: 
        break
avg_doc_len =  total_doc_len / total_no_of_doc
print "Average Document length is : {}".format(avg_doc_len)
