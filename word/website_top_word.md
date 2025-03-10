请你根据一个网站地址，计算出这个网站的高频单词，以及出现的次数和对应的例句。有如下要求：

1. 添加停用词文件
2. 输入指定数量的单词，按照出现频率倒排，设置初始值为 50
3. 添加短语过滤功能，单词长度小于 4 的单词直接过滤掉
4. 设置最小单词长度为 5
5. 为每一个单词搭配两个例句，并且例句来自于网站本身
6. 如下格式设置输出的格式：
单词：数量
例句1
例句2

单词：数量
例句1
例句2
7. 设置最大抓取网页的数量
8. 递归查询输入网页中包含的超链接，并且只查询那些 sub 网页，直接跳过不符合要求的超链接
9. 合并所有网页的词频，最终得到一个最终的词频
10. 添加一些安全措施，比方说更友好的错误处理，可以设置网络请求时间，超时时长设置，避免重复访问同一页面。
11. 将网页地址，最大抓取页数，单词最少长度，单数数量，这个参数全部都提取出来，这样我再次运行的时候方便修改

------------------------------- 下面的 Cursor 给我整理的需求文档 ----------------------------------
# Website Word Frequency Analyzer

## 1. Project Overview
A Python tool to analyze word frequencies from websites, with support for crawling multiple pages and providing context examples for each word.

## 2. Core Features
- Website crawling with configurable depth
- Word frequency analysis
- Context example extraction
- Stopwords filtering
- Configurable parameters
- Structured output format

## 3. Technical Requirements

### 3.1 Input Parameters
```python
CONFIG = {
    'url': str,              # Target website URL
    'max_pages': int,        # Maximum number of pages to crawl
    'min_word_length': int,  # Minimum word length to include
    'top_n_words': int,      # Number of top words to show
    'stopwords_file': str,   # Path to stopwords file
    'output_file': str,      # Path to output file
    'request_delay': int,    # Delay between requests (seconds)
    'request_timeout': int,  # Request timeout (seconds)
}
```

### 3.2 Required Python Libraries
- requests: For HTTP requests
- beautifulsoup4: For HTML parsing
- urllib: For URL handling
- collections: For data structures
- time: For request delays
- re: For text processing

## 4. Core Functions

### 4.1 URL Processing
- Check if URLs belong to same domain
- Verify if URL is subpage of base URL
- Handle URL joining and parsing

### 4.2 Word Processing
- Load and apply stopwords
- Extract words from text
- Filter words by length
- Count word frequencies
- Extract context examples

### 4.3 Web Crawling
- Respect max_pages limit
- Handle network errors
- Implement polite crawling delays
- Track visited pages
- Extract relevant links

## 5. Output Format
···
word1:frequency
context_example_1
context_example_2
word2:frequency
context_example_1
context_example_2
···

## 6. Error Handling
- Network connection errors
- File I/O errors
- Invalid URLs
- Timeout handling
- Missing stopwords file

## 7. Performance Considerations
- Implement request delays
- Limit concurrent connections
- Efficient text processing
- Memory usage optimization

## 8. Example Usage
```python
# Configure parameters
CONFIG = {
    'url': "https://example.com",
    'max_pages': 10,
    'min_word_length': 4,
    'top_n_words': 200,
    'stopwords_file': 'stopwords.txt',
    'output_file': 'results.txt',
    'request_delay': 1,
    'request_timeout': 10
}

# Run analysis
python word_frequency_analyzer.py
```

## 9. Expected Output
- Configuration display
- Crawling progress
- Word frequency results
- Context examples
- Error messages (if any)

## 10. Limitations and Constraints
- Respects robots.txt
- Single-threaded execution
- Plain text analysis only
- Memory-bound by available RAM
- Network-dependent performance