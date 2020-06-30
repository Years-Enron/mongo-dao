package mongo.dao.condition;

import org.bson.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;

/**
 * Mongo查询
 *
 * @author recall
 * @date 2018/11/18
 * @comment
 */
public class MongoQuery extends Query {

    private final String DEFAULT_SCORE_FIELD_FIELD_NAME = "textScore";
    private final Document META_TEXT_SCORE = new Document("$meta", "textScore");

    private String scoreFieldName = DEFAULT_SCORE_FIELD_FIELD_NAME;

    // 是否包含索引
    private boolean includeScore = false;
    // 是否通过全文索引相似度排序
    private boolean sortByScore = false;

    public MongoQuery() {
    }

    /**
     * Creates new {@link MongoQuery} for given {@link TextCriteria}.
     *
     * @param criteria 查询条件
     */
    public MongoQuery(TextCriteria criteria) {
        super(criteria);
    }

    /**
     * 使用相似度查询
     *
     * @return 排序查询
     * @see MongoQuery#includeScore()
     */
    public MongoQuery sortByScore() {
        this.includeScore();
        this.sortByScore = true;
        return this;
    }

    /**
     * 设置包含排序字段
     * @return 查询条件
     */
    public MongoQuery includeScore() {
        this.includeScore = true;
        return this;
    }

    /**
     * 根据指定的排序字段排序
     * @param fieldName 是排序字段名
     * @return 查询
     */
    public MongoQuery includeScore(String fieldName) {
        setScoreFieldName(fieldName);
        includeScore();
        return this;
    }

    /**
     * 设置排序属性名（避免与原文档内字段冲突）
     *
     * @param fieldName 排序属性名
     */
    public void setScoreFieldName(String fieldName) {
        this.scoreFieldName = fieldName;
    }

    /**
     * 获取排序属性名
     *
     * @return 排序属性名
     */
    public String getScoreFieldName() {
        return scoreFieldName;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.mongodb.core.query.Query#getFieldsObject()
     */
    @Override
    public Document getFieldsObject() {
        if (!this.includeScore) {
            return super.getFieldsObject();
        }
        Document fields = super.getFieldsObject();
        fields.put(getScoreFieldName(), META_TEXT_SCORE);
        return fields;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.mongodb.core.query.Query#getSortObject()
     */
    @Override
    public Document getSortObject() {
        Document sort = new Document();
        if (this.sortByScore) {
            sort.put(getScoreFieldName(), META_TEXT_SCORE);
        }
        sort.putAll(super.getSortObject());
        return sort;
    }

}
