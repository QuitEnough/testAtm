package ru.yana.DocumentRelation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.*;

@AllArgsConstructor
public class RecommenderServiceImpl<Document, User> implements RecommenderService<Document, User> {

    private final Scorer<Document, User> scorer;
    private final Map<String, DocumentGroup> documentGroups;
    private final List<Document> allDocuments;
    private final TProcessor processor;

    public RecommenderServiceImpl(Scorer<Document, User> scorer) {
        this.scorer = scorer;
        this.documentGroups = new HashMap<>();
        this.allDocuments = new ArrayList<>();
        this.processor = new TProcessor();
    }

    @Override
    public List<Document> getTop(User user, int limit) {
        if (limit <= 0) return Collections.emptyList();

        return allDocuments.stream()
                .sorted((doc1, doc2) ->
                        Double.compare(
                                scorer.getScore(doc1, user),
                                scorer.getScore(doc2, user)
                        ))
                .limit(limit)
                .toList();
    }

    @Override
    public void addDocument(Document document) {
        if (!(document instanceof TDocument tDoc)) {
            throw new IllegalArgumentException("the doc must be of type TDocument");
        }

        String url = tDoc.getUrl();

        var group = documentGroups.get(url);
        if (group == null) {
            group = new DocumentGroup(tDoc);
            documentGroups.put(url, group);

            var processedDoc = processor.process(group.getProcessedDocument());

            allDocuments.add((Document) processedDoc);
        } else {
            group.update(tDoc);

            for (int i = 0; i < allDocuments.size(); i++) {
                var doc = allDocuments.get(i);
                if (doc instanceof TDocument) {
                    var processedDoc = processor.process(group.getProcessedDocument());
                    allDocuments.set(i, (Document) processedDoc);
                    return;
                }
            }
        }
    }

}

interface RecommenderService<Document, User> {
    List<Document> getTop(User user, int limit);
    void addDocument(Document document);
}

interface Scorer<Document, User> {
    double getScore(Document doc, User user);
}

@Data
@AllArgsConstructor
class TDocument {
    private final String url;
    private final long pubDate;
    private final long fetchTime;
    private final String text;
    private Long firstFetchTime; // null изначально

    public boolean hasFirstFetchTime() {
        return firstFetchTime != null;
    }
}

@Getter
class DocumentGroup {
    private String url;
    private long pubDate;
    private long firstFetchTime;
    private TDocument latestDocument;

    public DocumentGroup(TDocument initialDoc) {
        this.url = initialDoc.getUrl();
        this.pubDate = initialDoc.getPubDate();
        this.firstFetchTime = initialDoc.getFetchTime();
        this.latestDocument = initialDoc;
    }

    public void update(TDocument doc) {
        if (doc.getFetchTime() < this.firstFetchTime) {
            this.firstFetchTime = doc.getFetchTime();
            this.pubDate = doc.getPubDate();
        }

        if (doc.getFetchTime() > this.latestDocument.getFetchTime()) {
            this.latestDocument = doc;
        }
    }

    public TDocument  getProcessedDocument() {
        TDocument processed = new TDocument(
                url,
                pubDate,
                latestDocument.getFetchTime(),
                latestDocument.getText(),
                firstFetchTime
        );
        return processed;
    }
}

class TProcessor {
    public TDocument process(TDocument input) {
        if (!input.hasFirstFetchTime()) {
            input.setFirstFetchTime(input.getFetchTime());
        }
        return input;
    }
}