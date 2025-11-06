package ru.yana.DocumentRelation;

import java.util.List;

public class RecommenderDemo {
    public static void main(String[] args) {
        Scorer<TDocument, String> scorer = (doc, user) ->
                doc.getText().toLowerCase().contains(user.toLowerCase()) ? 10.0 : 1.0;

        RecommenderService<TDocument, String> service = new RecommenderServiceImpl<>(scorer);

        service.addDocument(new TDocument("url1", 1000L, 3000L, "Старая версия новости"));
        service.addDocument(new TDocument("url2", 2000L, 4000L, "Технологии"));
        service.addDocument(new TDocument("url1", 1000L, 5000L, "Обновленная новость"));
        service.addDocument(new TDocument("url3", 3000L, 2000L, "Спорт"));

        List<TDocument> topDocs = service.getTop("новость", 2);
        System.out.println("Топ 2 документа:");
        topDocs.forEach(System.out::println);

        TDocument url1Doc = ((RecommenderServiceImpl<TDocument, String>) service).getDocumentByUrl("url1");
        System.out.println("\nАгрегированный url1:");
        System.out.println("PubDate: " + url1Doc.getPubDate() + " (от самой ранней версии)");
        System.out.println("FetchTime: " + url1Doc.getFetchTime() + " (от самой поздней версии)");
        System.out.println("FirstFetchTime: " + url1Doc.getFirstFetchTime() + " (минимальный)");
        System.out.println("Text: '" + url1Doc.getText() + "' (от самой поздней версии)");
    }
}
