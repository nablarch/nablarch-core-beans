package nablarch.core.beans;

import nablarch.core.util.StringUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ネストしたプロパティを表すクラス。
 *
 * @author T.Kawasaki
 */
class PropertyExpression {

    /** ネストしたプロパティ */
    private final List<String> nestedProperties;

    /** リストまたは配列型プロパティに関する情報 */
    private final ListPropertyInfo listPropertyInfo;

    /** リストまたは配列型プロパティのプロパティ名、要素番号を抽出するためのパターン. */
    private static final Pattern PATTERN = Pattern.compile("(.*)\\[(\\d+)]$");

    /** ネストしたプロパティの文字列表現（ドット区切り） */
    private final String rawKey;

    /**
     * コンストラクタ。
     *
     * @param nestedProperties ネストしたプロパティ
     */
    private PropertyExpression(List<String> nestedProperties) {
        if (nestedProperties.isEmpty()) {
            throw new IllegalArgumentException("invalid.");
        }
        this.nestedProperties = nestedProperties;
        this.listPropertyInfo = createListPropertyInfo();
        this.rawKey = String.join(".", nestedProperties);
    }

    /**
     * コンストラクタ。
     *
     * @param expression ネストしたプロパティの文字列表現（ドット区切り）
     */
    PropertyExpression(String expression) {
        if (StringUtil.isNullOrEmpty(expression)) {
            throw new IllegalArgumentException("expression is null or blank.");
        }
        this.nestedProperties = new LinkedList<>(Arrays.asList(expression.split("\\.")));
        this.listPropertyInfo = createListPropertyInfo();
        this.rawKey = expression;
    }

    /**
     * {@link ListPropertyInfo}のインスタンスを生成する。
     *
     * @return {@link ListPropertyInfo}のインスタンス
     */
    private ListPropertyInfo createListPropertyInfo() {
        final String root = getRoot();
        if (root.contains("[")) {
            Matcher matcher = PATTERN.matcher(root);
            if (matcher.matches()) {
                return new ListPropertyInfo(true, matcher.group(1), Integer.parseInt(matcher.group(2)));
            }
        }
        return new ListPropertyInfo(false, null, 0);
    }

    /**
     * ルート要素の文字列表現を取得する。
     * "aaa.bbb.ccc"のとき、"aaa"が返却される。
     *
     * @return ルート要素の文字列表現
     */
    String getRoot() {
        return nestedProperties.get(0);
    }

    /**
     * 本インスタンスが表すプロパティがネストしたプロパティかどうか判定する。
     * 例："aaa.bbb"の場合、真
     *
     * @return ネストしたプロパティの場合、真
     */
    boolean isNested() {
        return nestedProperties.size() > 1;
    }

    /**
     * 本インスタンスが表すプロパティが単一のプロパティ（ネストしていない）かどうか判定する。<br/>
     * 例："bbb"の場合、真
     *
     * @return 単一のプロパティの場合、真
     */
    boolean isNode() {
        return !isNested();
    }

    /**
     * 子の{@link PropertyExpression}を取得する。
     * 本インスタンスが"aaa.bbb.ccc"のとき、"bbb.ccc"のインスタンスが返却される。
     *
     * @return 子のPropertyExpression
     */
    PropertyExpression rest() {
        List<String> rest = nestedProperties.subList(1, nestedProperties.size());
        return new PropertyExpression(rest);
    }

    /**
     * 単純なプロパティか判定する.<br/>
     * @return リストまたは配列のプロパティではない場合、真
     */
    boolean isSimpleProperty() {
        return !isListOrArray();
    }

    /**
     * リストまたは配列か判定する.<br/>
     * 例："xxx[0]"の場合、真
     * @return リストまたは配列のプロパティに相当する場合、真
     */
    boolean isListOrArray() {
        return listPropertyInfo.isListProperty();
    }

    /**
     * 要素番号を返す.<br/>
     * 例："xxx[0]"の「0」の部分
     * @return 要素番号
     */
    int getListIndex() {
        return listPropertyInfo.getListPropertyIndex();
    }

    /**
     * リストまたは配列のプロパティ名を返す.<br/>
     * 例："xxx[0]"の「xxx」の部分
     * @return プロパティ名
     */
    String getListPropertyName() {
        return listPropertyInfo.getListPropertyName();
    }

    /**
     * {@link PropertyExpression#rawKey}を返却する。
     *
     * @return rawKey
     */
    String getRawKey() {
        return rawKey;
    }

    /**
     * リストまたは配列型プロパティに関する情報をまとめたJava Beansクラス。
     *
     * @author Naoki Yamamoto
     */
    private static class ListPropertyInfo {

        /** リストまたは配列型プロパティか否か */
        private final boolean isListProperty;

        /** リストまたは配列型プロパティ名 */
        private final String listPropertyName;

        /** リストまたは配列型のプロパティの要素番号 */
        private final int listPropertyIndex;

        /**
         * コンストラクタ。
         *
         * @param isListProperty リストまたは配列型プロパティか否か
         * @param listPropertyName リストまたは配列型プロパティ名
         * @param listPropertyIndex リストまたは配列型のプロパティの要素番号
         */
        public ListPropertyInfo(boolean isListProperty, String listPropertyName, int listPropertyIndex) {
            this.isListProperty = isListProperty;
            this.listPropertyName = listPropertyName;
            this.listPropertyIndex = listPropertyIndex;
        }

        /**
         * リストまたは配列型プロパティか否かを取得する。
         *
         * @return リストまたは配列型プロパティか否か
         */
        public boolean isListProperty() {
            return isListProperty;
        }

        /**
         * リストまたは配列型プロパティ名を取得する。
         *
         * @return リストまたは配列型プロパティ名
         */
        public String getListPropertyName() {
            return listPropertyName;
        }

        /**
         * リストまたは配列型のプロパティの要素番号を取得する。
         *
         * @return リストまたは配列型のプロパティの要素番号
         */
        public int getListPropertyIndex() {
            return listPropertyIndex;
        }
    }
}
