from elasticsearch import Elasticsearch
es = Elasticsearch()

es.put_script(
            lang = "groovy",
            id = "calTF",
            body = {
                    "script" : "_index[field][term].tf()"
                }
            )