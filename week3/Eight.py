#!/usr/bin/env python
import re, sys, operator

# Mileage may vary. If this crashes, make it lower
RECURSION_LIMIT = 5000
# We add a few more, because, contrary to the name,
# this doesn't just rule recursion: it rules the 
# depth of the call stack
sys.setrecursionlimit(RECURSION_LIMIT+10)

def count(word_list, stopwords, wordfreqs):
    # What to do with an empty list
    if word_list == []:
        return
    # The inductive case, what to do with a list of words
    else:
        # Process the head word
        word = word_list[0]
        if word not in stopwords:
            if word in wordfreqs:
                wordfreqs[word] += 1
            else:
                wordfreqs[word] = 1
        # Process the tail 
        count(word_list[1:], stopwords, wordfreqs)

def wf_print(wordfreq):
    if wordfreq == []:
    	return
    else:
    	(w, c) = wordfreq[0]
    	print(w, '-', c)
    	wf_print(wordfreq[1:])

def partition(wordfreq, low, high): 
	# desc 
	i = low - 1
	# choose the last element as the pivot to sort
	pivot = wordfreq[high][1]
	for j in range(low, high):
		if wordfreq[j][1] >= pivot: 
			i = i + 1 
			wordfreq[i], wordfreq[j] = wordfreq[j], wordfreq[i] 
	wordfreq[i + 1], wordfreq[high] = wordfreq[high], wordfreq[i + 1]
	# return the index of 
	return (i + 1)

def quick_sort(wordfreq, low, high):
	if low >= high:
		return wordfreq
	if low < high:
		pi = partition(wordfreq, low, high)
		quick_sort(wordfreq, low, pi - 1)
		quick_sort(wordfreq, pi + 1, high)
	return wordfreq

stop_words = set(open('../stop_words.txt').read().split(','))
words = re.findall('[a-z]{2,}', open(sys.argv[1]).read().lower())
word_freqs = {}
# Theoretically, we would just call count(words, stop_words, word_freqs)
# Try doing that and see what happens.
for i in range(0, len(words), RECURSION_LIMIT):
    count(words[i:i+RECURSION_LIMIT], stop_words, word_freqs)

word_final = list(word_freqs.items())
# print(word_final[0:25])
wf_print(quick_sort(word_final, 0, len(word_final) - 1)[:25])