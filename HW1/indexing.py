from elasticsearch import Elasticsearch
import glob
es = Elasticsearch()



es.indices.delete(index='documents')

# Creating the index on elasticsearch server
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

# Reading the documents to create index of documents.
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
                    doc = {
                        'docno' : doc_no,
                        'text': line_array
                        }
                    # Creating the index of document
                    res = es.index(index="documents", doc_type='stories', id=doc_no, body=doc)
                    line_array = []
                    
                    
# After indexing, Calculating the average and each document length and saving in file for future use.

doc_path = "/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/doclist.txt"
es = Elasticsearch()
doc_ids = []
total_doc_len = 0
total_no_of_doc = 0
docs_length = {}
with open(doc_path) as data_file:  
    for line in data_file:
        words = line.split()
        if (words[0] == '0'):
            continue
        doc_ids.append(words[1])
        total_no_of_doc += 1

counter = 0
extra = 10
while counter < total_no_of_doc:
    ids = doc_ids[counter:(counter + extra)]
    rs = es.mtermvectors(
            index="documents",
            doc_type="stories",
            ids=ids,
            field_statistics = "false",
            offsets="false",
            payloads="false",
            positions="false"
        )
    
    for doc in rs["docs"]:
        doc_length = 0
        if len(doc["term_vectors"]) != 0 :          
            for term in doc["term_vectors"]["text"]["terms"]:
                doc_length += doc["term_vectors"]["text"]["terms"][term]["term_freq"]
        total_doc_len += doc_length
        docs_length[doc['_id']]=doc_length
    
    counter +=  extra
    diff = total_no_of_doc - counter
    if diff < 10 and diff > 0:
        extra = diff

avg_doc_len =  total_doc_len / total_no_of_doc
print "Average Document length is : {}".format(avg_doc_len)
print "value of scroll size is : {}".format(total_no_of_doc)

file = open("/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/doclengths.txt", "w")
for doc_no, doc_length in docs_length.iteritems():
        file.write(str(doc_no) +" "+str(doc_length)+"\n")
file.close()






