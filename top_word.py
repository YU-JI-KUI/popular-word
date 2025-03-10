import requests
from bs4 import BeautifulSoup
from collections import defaultdict
from urllib.parse import urljoin, urlparse
import time
import re
import sys

# Configuration parameters
CONFIG = {
    'url': "https://docs.spring.io/spring-ai/reference/index.html",  # Target website URL
    'max_pages': 20,                                                # Maximum number of pages to crawl
    'min_word_length': 5,                                          # Minimum word length to include
    'top_n_words': 50,                                            # Number of top words to show
    'stopwords_file': 'word/stopwords.txt',                        # Path to stopwords file
    'output_file': 'word/top_words.txt',                           # Path to output file
    'request_delay': 2,                                            # Delay between requests (seconds)
    'request_timeout': 10,                                         # Request timeout (seconds)
}

def is_same_domain(url1, url2):
    """Check if two URLs belong to the same domain"""
    return urlparse(url1).netloc == urlparse(url2).netloc

def is_subpage(base_url, url):
    """Check if URL is a subpage of the base URL"""
    base_path = urlparse(base_url).path
    url_path = urlparse(url).path
    return url_path.startswith(base_path)

def load_stopwords(file_path=CONFIG['stopwords_file']):
    """Load stopwords from file"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            stopwords = set(word.strip().lower() for word in f)
            print(f"Loaded {len(stopwords)} stopwords from {file_path}")
            return stopwords
    except FileNotFoundError:
        print(f"Warning: Stopwords file not found at {file_path}")
        return set()
    except Exception as e:
        print(f"Error loading stopwords: {str(e)}")
        return set()

def process_page(url, stopwords, min_word_length=CONFIG['min_word_length']):
    try:
        print(f"Fetching content from {url}")
        response = requests.get(url, timeout=CONFIG['request_timeout'])
        response.raise_for_status()
        
        print("Parsing HTML content")
        soup = BeautifulSoup(response.text, 'html.parser')
        
        # Extract text content
        text = soup.get_text()
        words = text.lower().split()
        print(f"Found {len(words)} words on page")
        
        # Count word frequencies and collect examples
        word_freq = defaultdict(int)
        word_examples = defaultdict(list)
        
        # Process each word and its context
        for i, word in enumerate(words):
            word = word.strip('.,!?()[]{}":;')
            if word and len(word) >= min_word_length and word not in stopwords:
                word_freq[word] += 1
                
                # Get context (20 chars before and after)
                start = max(0, i - 5)
                end = min(len(words), i + 6)
                context = ' '.join(words[start:end])
                
                # Only add meaningful examples
                if len(context) > 20 and context not in word_examples[word]:
                    word_examples[word].append(context)
        
        print(f"Found {len(word_freq)} unique words meeting criteria")
        return word_freq, word_examples
        
    except requests.exceptions.RequestException as e:
        print(f"Network error processing {url}: {str(e)}")
        return defaultdict(int), defaultdict(list)
    except Exception as e:
        print(f"Error processing {url}: {str(e)}")
        return defaultdict(int), defaultdict(list)

def crawl_website(start_url, max_pages=CONFIG['max_pages'], min_word_length=CONFIG['min_word_length']):
    print(f"Starting crawl with max_pages={max_pages}, min_word_length={min_word_length}")
    stopwords = load_stopwords()
    visited = set()
    to_visit = {start_url}
    word_freq = defaultdict(int)
    word_examples = defaultdict(list)
    
    print(f"Starting to crawl {start_url} (max {max_pages} pages)...")
    
    while to_visit and len(visited) < max_pages:
        url = to_visit.pop()
        if url in visited:
            continue
            
        print(f"\nProcessing page {len(visited) + 1}/{max_pages}: {url}")
        visited.add(url)
        
        try:
            response = requests.get(url, timeout=CONFIG['request_timeout'])
            response.raise_for_status()
            soup = BeautifulSoup(response.text, 'html.parser')
            
            # Process current page
            page_freq, page_examples = process_page(url, stopwords, min_word_length)
            
            # Merge word frequencies and examples
            for word, freq in page_freq.items():
                word_freq[word] += freq
                # Keep only the first two unique examples for each word
                for example in page_examples[word]:
                    if example not in word_examples[word] and len(word_examples[word]) < 2:
                        word_examples[word].append(example)
            
            # Extract and filter links (only subpages)
            links = []
            for link in soup.find_all('a', href=True):
                href = link['href']
                full_url = urljoin(url, href)
                if is_same_domain(start_url, full_url) and is_subpage(start_url, full_url) and full_url not in visited:
                    links.append(full_url)
            
            to_visit.update(links)
            print(f"Found {len(links)} new subpages to visit")
            
            time.sleep(CONFIG['request_delay'])  # Be nice to the server
            
        except Exception as e:
            print(f"Error crawling {url}: {str(e)}")
            continue
    
    print(f"\nCrawl completed. Visited {len(visited)} pages.")
    return word_freq, word_examples

def main():
    try:
        # Print configuration
        print("\nCurrent Configuration:")
        print("-" * 50)
        for key, value in CONFIG.items():
            print(f"{key}: {value}")
        print("-" * 50)
        
        word_freq, word_examples = crawl_website(
            CONFIG['url'],
            max_pages=CONFIG['max_pages'],
            min_word_length=CONFIG['min_word_length']
        )
        
        # Sort words by frequency
        sorted_words = sorted(word_freq.items(), key=lambda x: x[1], reverse=True)
        print(f"\nFound {len(sorted_words)} unique words")
        
        # Print results
        print(f"\nTop {CONFIG['top_n_words']} most frequent words (excluding stopwords, min length: {CONFIG['min_word_length']}):")
        print("-" * 100)
        
        with open(CONFIG['output_file'], 'w', encoding='utf-8') as f:
            for word, count in sorted_words[:CONFIG['top_n_words']]:
                # Write word and count
                output = f"{word}:{count}\n"
                print(output, end='')
                f.write(output)
                
                # Write examples
                examples = word_examples[word][:2]
                if examples:
                    for example in examples:
                        output = f"{example}\n"
                        print(output, end='')
                        f.write(output)
                else:
                    output = "No examples found\n"
                    print(output, end='')
                    f.write(output)
                
                # Add a blank line between words
                output = "\n"
                print(output, end='')
                f.write(output)
        
        print(f"\nResults have been saved to {CONFIG['output_file']}")
        
    except Exception as e:
        print(f"An unexpected error occurred: {str(e)}")
        sys.exit(1)

if __name__ == "__main__":
    main() 