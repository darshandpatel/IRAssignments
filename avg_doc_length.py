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
scroll_size = total_no_of_doc


while (scroll_size > 0):
    try:
        res = es.scroll(scroll_id=scroll_id, scroll='10s')
        
        for story in res['hits']['hits']:
            rs = es.termvector(
                index="documents",
                doc_type="stories",
                id=story["_id"],
                field_statistics = "false"
            )
            
            doc_length = 0
            for term in rs["term_vectors"]["text"]["terms"]:
                doc_length += rs["term_vectors"]["text"]["terms"][term]["term_freq"]
            
            total_doc_len += doc_length
            scroll_size -= 1
        scroll_id = res['_scroll_id']
    except: 
        break
avg_doc_len =  total_doc_len / total_no_of_doc
print "Average Document length is : {}".format(avg_doc_len)
