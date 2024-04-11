package com.app.sns.aiproduct.config;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * 代码生成
 *
 */
public class MybatisGenerator {

    public static void main(String[] args) {
        generator("***",//作者信息
                "jdbc:postgresql:",//这里键入你的数据库信息
                "org.postgresql.Driver",//数据库驱动器
                "*****",//数据库账号
                "********",//数据库密码
                "com.***",//项目包名
                "",
                "",
                "*****",//数据库表名称
                "",
                new String[]{});
    }

    /**
     * 代码生成器
     *
     * @param author         作者
     * @param url            url,不需要带问号
     * @param driverName     驱动名称
     * @param username       用户名
     * @param password       密码
     * @param parent         父级类名,com.zjugis.xxx
     * @param projectModule  子模块名称,存在maven父子项目时填写子模块名称
     * @param functionModule 功能模块,用于包分类
     * @param tableName      表名,多个表名用逗号分隔
     * @author 朝来试看青枝上
     * @date 2023/04/13
     */
    public static void generator(String author,
                                 String url,
                                 String driverName,
                                 String username,
                                 String password,
                                 String parent,
                                 String projectModule,
                                 String functionModule,
                                 String tableName,
                                 String parentEntityClass,
                                 String[] parentEntityColumns) {
        AutoGenerator mpg = new AutoGenerator();
        mpg.setGlobalConfig(globalConfig(author, projectModule));
        mpg.setDataSource(dataSourceConfig(url, driverName, username, password));
        mpg.setPackageInfo(packageConfig(parent, functionModule));
        mpg.setCfg(injectionConfig(functionModule, projectModule));
        mpg.setTemplate(templateConfig());
        mpg.setStrategy(strategyConfig(tableName, parentEntityClass, parentEntityColumns));
        mpg.execute();
    }

    /**
     * 全局配置
     *
     * @param author        作者
     * @param projectModule 项目模块
     * @return com.baomidou.mybatisplus.generator.config.GlobalConfig
     * @author 朝来试看青枝上
     * @date 2023/04/13
     */
    private static GlobalConfig globalConfig(String author, String projectModule) {
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        if (!"".equals(projectModule)) {
            //多模块工程路径设置
            gc.setOutputDir(projectPath + "/" + projectModule + "/src/main/java");
        } else {
            gc.setOutputDir(projectPath + "/src/main/java");
        }
        gc.setAuthor(author);
        gc.setOpen(false);
        //实体属性 Swagger2 注解
        gc.setSwagger2(true);
        return gc;
    }

    /**
     * 数据源设置
     *
     * @param url        驱动连接的URL
     * @param driverName 驱动名称
     * @param username   数据库连接用户名
     * @param password   数据库连接密码
     * @return DataSourceConfig
     */
    private static DataSourceConfig dataSourceConfig(String url,
                                                     String driverName,
                                                     String username,
                                                     String password) {
        DataSourceConfig dsc = new DataSourceConfig();
//        if (url.contains("?")) {
        dsc.setUrl(url);
//        } else {
//            dsc.setUrl(url + "?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=UTC");
//        }
        dsc.setDriverName(driverName);
        dsc.setUsername(username);
        dsc.setPassword(password);
        return dsc;
    }


    /**
     * 包配置
     *
     * @param parent         父包名。如果为空，将下面子包名必须写全部， 否则就只需写子包名
     * @param functionModule 功能模块包名
     * @return PackageConfig
     */
    private static PackageConfig packageConfig(String parent, String functionModule) {
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent(parent);
        if (!"".equals(functionModule)) {
            packageConfig.setEntity("entity." + functionModule);
            packageConfig.setService("service." + functionModule);
            packageConfig.setServiceImpl("service.impl." + functionModule);
            packageConfig.setMapper("mapper." + functionModule);
            packageConfig.setController("controller." + functionModule);
        } else {
            packageConfig.setParent(parent);
        }

        return packageConfig;
    }

    /**
     * 自定义配置，设置xml文件路径以及输出格式
     *
     * @param functionModule 功能模块名称
     * @return com.baomidou.mybatisplus.generator.InjectionConfig
     * @author 朝来试看青枝上
     * @date 2023/04/13
     */
    private static InjectionConfig injectionConfig(String functionModule, String projectModule) {

        String projectPath = System.getProperty("user.dir");
        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 velocity
        String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                if (!"".equals(projectModule)) {
                    // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                    return projectPath + projectModule + "/src/main/resources/mapper/" + functionModule
                            + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
                } else {
                    // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                    return projectPath + "/src/main/resources/mapper/" + functionModule
                            + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
                }
            }
        });

        cfg.setFileOutConfigList(focList);

        return cfg;
    }


    /**
     * 模板路径配置项
     *
     * @return com.baomidou.mybatisplus.generator.config.TemplateConfig
     * @author 朝来试看青枝上
     * @date 2023/04/13
     */
    private static TemplateConfig templateConfig() {
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setXml(null);
        return templateConfig;
    }

    private static StrategyConfig strategyConfig(String tableName, String parentEntityClass, String[] parentEntityColumns) {
        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        if (parentEntityClass != null
                && !"".equals(parentEntityClass)
                && parentEntityColumns != null
                && parentEntityColumns.length > 0) {
            // 公共父类
            strategy.setSuperEntityClass(parentEntityClass);
            // 写于父类中的公共字段
            strategy.setSuperEntityColumns(parentEntityColumns);
        }
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
//        strategy.setSuperControllerClass("你自己的父类控制器,没有就不用设置!");
        if (!"".equals(tableName)) {
            strategy.setInclude(tableName.split(","));
        }
        strategy.setControllerMappingHyphenStyle(true);
//        strategy.setTablePrefix(pc.getModuleName() + "_");
        return strategy;
    }

}
