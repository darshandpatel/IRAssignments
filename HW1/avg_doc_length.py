from elasticsearch import Elasticsearch
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
