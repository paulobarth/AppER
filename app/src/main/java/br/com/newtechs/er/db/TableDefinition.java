package br.com.newtechs.er.db;

import android.provider.BaseColumns;

/**
 * Created by Paulo on 03/10/2015.
 */
public final class TableDefinition {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public TableDefinition() {}

    /* Inner class that defines the table contents */

    //Tabela para guardar os relatórios
    public static abstract class Report implements BaseColumns {
        public static final String TABLE_NAME           = "report";
        public static final String COLUMN_NAME_CODIGO   = "codigo";
        public static final String COLUMN_NAME_TIPO_ORIGEM = "tipoOrigem";
        public static final String COLUMN_NAME_ORIGEM_DADOS = "origemDados";
        public static final String COLUMN_NAME_FORMATO_ORIGEM = "formatoOrigem";
        public static final String COLUMN_NAME_TIPO_ATZ = "tipoAtz";
        public static final String COLUMN_NAME_NOME     = "nome";
        public static final String COLUMN_NAME_LAYOUT   = "layout";
        public static final String COLUMN_NAME_VISIVEL  = "visivel";
        public static final String COLUMN_NAME_USER_LOGIN_COL  = "userLoginCol";
        public static final String COLUMN_NAME_USER_ID  = "userID";
        public static final String COLUMN_NAME_SHARED  = "shared";
    }

    //Tabela para guardar a estrutura dos relatórios, as colunas
    public static abstract class Column implements BaseColumns {
        public static final String TABLE_NAME                   = "columns";
        public static final String COLUMN_NAME_REPORT           = "report";
        public static final String COLUMN_NAME_CODIGO           = "codigo";
        public static final String COLUMN_NAME_LABEL            = "label";
        public static final String COLUMN_NAME_PARAM            = "param";
        public static final String COLUMN_NAME_FILTRO           = "filtro";
    }

    //Tabela para guardar o conteúdo da tabela
    public static abstract class ContentReport implements BaseColumns {
        public static final String TABLE_NAME               = "contentReport";
        public static final String COLUMN_NAME_REPORT       = "report";
        public static final String COLUMN_NAME_LINE         = "line";
        public static final String COLUMN_NAME_COLUMN       = "column";
        public static final String COLUMN_NAME_VALUE        = "value";
    }

    //Tabela para guardar a referencia de cada célula do Layout com a Coluna do Relatório
    public static abstract class LayoutReport implements BaseColumns {
        public static final String TABLE_NAME               = "layoutReport";
        public static final String COLUMN_NAME_REPORT       = "report";
        public static final String COLUMN_NAME_LAYOUT       = "layout";
        public static final String COLUMN_NAME_COLPOSITION  = "colPosition";
        public static final String COLUMN_NAME_COLUMN       = "column";
    }

    //Tabela para guardar a lista de email para compartilhar o relatorio
    public static abstract class ShareReport implements BaseColumns {
        public static final String TABLE_NAME               = "shareReport";
        public static final String COLUMN_NAME_REPORT       = "report";
        public static final String COLUMN_NAME_EMAIL        = "email";
    }

    //Tabela para guardar os Layouts Padroes da aplicação
    public static abstract class Layout implements BaseColumns {
        public static final String TABLE_NAME               = "layout";
        public static final String COLUMN_NAME_CODIGO       = "codigo";
        public static final String COLUMN_NAME_QTROW        = "qtRow";
        public static final String COLUMN_NAME_QTCOL        = "qtCol";
    }

    //Tabela para guardar os usuários que já logaram e seu ultimo password utilizado
    //para permitir logar quando não houver internet
    public static abstract class UserLogin implements BaseColumns {
        public static final String TABLE_NAME           = "userLogin";
        public static final String COLUMN_NAME_USER     = "user";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_EMAIL    = "email";
        public static final String COLUMN_NAME_DATA_ULT_ACESSO = "dataUltAcesso";
        public static final String COLUMN_NAME_COMPANY_NAME = "companyName";
    }
}