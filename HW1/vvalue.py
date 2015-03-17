from elasticsearch import Elasticsearch
es = Elasticsearch()


res = es.search(
        index="documents",
        body={
                "aggs" : {
                    "unique_terms" : {
                        "cardinality" : {
                            "field" : "text"
                        
                        }
                    }
                }
            }
        )

unique_terms = res["aggregations"]["unique_terms"]["value"]
print "Number of unique terms in corpos is : {}".format(unique_terms)