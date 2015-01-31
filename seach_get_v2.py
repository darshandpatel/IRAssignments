from elasticsearch import Elasticsearch
from elasticsearch import helpers
es = Elasticsearch()

result = helpers.scan(es, 
                    index='documents',
                    query={
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
                    },
                    doc_type='stories'
                )


print result

