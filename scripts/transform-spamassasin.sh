#!/bin/bash
# This script extracts the messages contained in spamAssassin corpus
# from the raw files included in the tarballs (tar.bz2) that can be 
# downloaded from https://spamassassin.apache.org/old/publiccorpus/
# Ham emails are included in _ham_ subfolder
# while spam files are included in a _spam_ subfolder
# Usage:
# 1- Download all tarballs (.tar.bz2) from the URL specified above 
# 3- Move this file into the directory where the tarballs have been
#    placed 
#     $ mv transform-spamassasin.sh sa
# 4- Execute this file in the target directory
#     $ cd sa
#     $ chmod +x transform-spamassasin.sh
#     $ ./transform-spamassasin.sh
# 5- The output will be generated in the folder spamassassin_corpus

OUTPUT_DIR="spamassassin_corpus"
SPAM_DIR="${OUTPUT_DIR}/_spam_/"
HAM_DIR="${OUTPUT_DIR}/_ham_/"

mkdir -p ${SPAM_DIR}
mkdir -p ${HAM_DIR}

for i in 20030228_spam.tar.bz2 20050311_spam_2.tar.bz2 
do
   tar xjvf $i --strip-components 1 --directory ${SPAM_DIR} --exclude=cmds
done

for i in 20030228_easy_ham_2.tar.bz2 20030228_easy_ham.tar.bz2 20030228_hard_ham.tar.bz2
do
   tar xjvf $i --strip-components 1 --directory ${HAM_DIR} --exclude=cmds
done

echo "Done converting. Converted: "
echo "+ $(find ${SPAM_DIR} -type f | wc -l)  spam messages in ${SPAM_DIR}"
echo "+ $(find ${HAM_DIR} -type f | wc -l) ham messages in ${HAM_DIR}"
echo "TOTAL: $(find ${OUTPUT_DIR} -type f | wc -l)"
