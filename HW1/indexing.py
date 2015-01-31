from elasticsearch import Elasticsearch
import glob
es = Elasticsearch()

es.indices.delete(index='documents')


es.indices.create(
    index='documents',
    body={
        'mappings' : {
            'stories' : {
                'properties' : {
                    "docno": {
                        "type": "string",
                        "store": "true",
                        "index": "not_analyzed"
                    },
                    "text": {
                        "type": "string",
                        "store": "true",
                        "index": "analyzed",
                        "term_vector": "with_positions_offsets_payloads",
                        "analyzer": "my_english"
                    }
                }
            }
        },
        "settings": {
            "index": {
                "store": {
                    "type": "default"
                },
                "number_of_shards": 1,
                "number_of_replicas": 1
            },
            "analysis": {
                "analyzer": {
                    "my_english": { 
                        "type": "english",
                        "stopwords_path": "/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/stoplist.txt" 
                    }
                }
            }
        }
    }
)

count = 0
for file_path in glob.glob("/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/ap89_collection/*"):
    
    if 'readme' not in file_path:
        with open(file_path) as data_file:
    
            text_started = False
            doc_no = 0
            line_array = []
            for line in data_file:
                if '<DOCNO>' in line:
                    words = line.split()
                    doc_no = words[1]
                elif '<TEXT>' in line:
                    text_started = True
                elif text_started:
                    if '</TEXT>' not in line:
                        line_array.append(line)
                    else:
                        text_started = False
                elif '</DOC>' in line:
                    if len(line_array) != 0:
                        count+=1
                        doc = {
                            'docno' : doc_no,
                            'text': line_array
                            }
                        res = es.index(index="documents", doc_type='stories', id=doc_no, body=doc)
                    line_array = []
                    #print (res)
                
print 'Total number of indexing created is :' +str(count)







