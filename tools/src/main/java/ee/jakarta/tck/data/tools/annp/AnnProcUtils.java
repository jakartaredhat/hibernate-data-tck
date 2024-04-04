/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package ee.jakarta.tck.data.tools.annp;

import ee.jakarta.tck.data.tools.qbyn.ParseUtils;
import ee.jakarta.tck.data.tools.qbyn.QueryByNameInfo;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Save;
import jakarta.data.repository.Update;
import org.stringtemplate.v4.ST;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class AnnProcUtils {
    static String REPO_TEMPLATE = """
            package #repo.pkg#;
            import jakarta.annotation.Generated;
            import jakarta.data.repository.OrderBy;
            import jakarta.data.repository.Query;
            import jakarta.data.repository.Repository;
            import #repo.fqn#;

            @Repository(dataStore = "#repo.dataStore#")
            @Generated("ee.jakarta.tck.data.tools.annp.RespositoryProcessor")
            public interface #repo.name#$ extends #repo.name# {
                #repo.methods :{m |
                    @Override
                    @Query("#m.query#")
                    #m.orderBy :{o | @OrderBy(value="#o.property#", descending = #o.descending#)}#
                    public #m.returnType# #m.name# (#m.parameters: {p | #p#}; separator=", "#);
                    
                    }
            	#
            }
            """;

    /**
     *
     * @param typeElement
     * @return
     */
    public static List<ExecutableElement> methodsIn(TypeElement typeElement) {
        ArrayList<ExecutableElement> methods = new ArrayList<>();
        List<ExecutableElement> typeMethods = methodsIn(typeElement.getEnclosedElements());
        methods.addAll(typeMethods);
        List<? extends TypeMirror> superifaces = typeElement.getInterfaces();
        for (TypeMirror iface : superifaces) {
            if(iface instanceof DeclaredType) {
                DeclaredType dt = (DeclaredType) iface;
                System.out.printf("Processing superinterface %s<%s>\n", dt.asElement(), dt.getTypeArguments());
                methods.addAll(methodsIn((TypeElement) dt.asElement()));
            }
        }
        return methods;
    }
    public static List<ExecutableElement> methodsIn(Iterable<? extends Element> elements) {
        ArrayList<ExecutableElement> methods = new ArrayList<>();
        for (Element e : elements) {
            if(e.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) e;
                // Skip lifecycle methods
                if(!isLifeCycleMethod(method)) {
                    methods.add(method);
                }
            }
        }
        return methods;
    }

    /**
     * Is a method annotated with a lifecycle annotation
     * @param method a repository method
     * @return true if the method is a lifecycle method
     */
    public static boolean isLifeCycleMethod(ExecutableElement method) {
        return method.getAnnotation(Insert.class) != null
                || method.getAnnotation(Find.class) != null
                || method.getAnnotation(Update.class) != null
                || method.getAnnotation(Save.class) != null
                || method.getAnnotation(Delete.class) != null;
    }

    public static String getFullyQualifiedName(Element element) {
        if (element instanceof TypeElement) {
            return ((TypeElement) element).getQualifiedName().toString();
        }
        return null;
    }


    public static QueryByNameInfo isQBN(ExecutableElement m) {
        QueryByNameInfo info = null;
        String methodName = m.getSimpleName().toString();
        if(methodName.startsWith("findBy") || methodName.startsWith("findFirst") || methodName.startsWith("deleteBy")
                || methodName.startsWith("updateBy")  || methodName.startsWith("countBy")
                || methodName.startsWith("existsBy") ) {
            try {
                info = ParseUtils.parseQueryByName(methodName);
            } catch (Throwable e) {
                System.out.printf("Failed to parse %s: %s\n", methodName, e.getMessage());
                // TODO, need to handle simple xxxBy methods that don't follow parse grammar
                return null;
            }
            return info;
        }

        return null;
    }

    public static void writeRepositoryInterface(RepositoryInfo repo, ProcessingEnvironment processingEnv) throws IOException {
        ST st = new ST(REPO_TEMPLATE, '#', '#');
        st.add("repo", repo);
        String ifaceSrc = st.render();
        String ifaceName = repo.getFqn() + "$";
        Filer filer = processingEnv.getFiler();
        JavaFileObject srcFile = filer.createSourceFile(ifaceName, repo.getRepositoryElement());
        try(Writer writer = srcFile.openWriter()) {
            writer.write(ifaceSrc);
            writer.flush();
        }
        System.out.printf("Wrote %s, to: %s\n", ifaceName, srcFile.toUri());
    }
}
