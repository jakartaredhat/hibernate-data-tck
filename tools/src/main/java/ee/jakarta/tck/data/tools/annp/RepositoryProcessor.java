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

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ee.jakarta.tck.data.tools.qbyn.QueryByNameInfo;
import jakarta.data.repository.Repository;
import jakarta.persistence.Entity;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;


/**
 * Annotation processor for {@link Repository} annotations that creates sub-interfaces for repositories
 * that use Query By Name (QBN) methods.
 *
 * Options:
 * providerFilter - the JPA provider to filter against, defaults to empty which allows all providers
 */
@SupportedAnnotationTypes("jakarta.data.repository.Repository")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedOptions({"providerFilter"})
public class RepositoryProcessor extends AbstractProcessor {
    private Map<String, RepositoryInfo> repoInfoMap = new HashMap<>();
    private String providerFilter = "";
    private ProcessorLogger log;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        log = new ProcessorLogger(processingEnv);
        log.info("RepositoryProcessor initialized");
        Map<String, String> options = processingEnv.getOptions();
        if (options.containsKey("providerFilter")) {
            providerFilter = options.get("providerFilter");
            log.info(String.format("Provider filter set to %s\n", providerFilter));
        }
    }

    /**
     * Process the repositories annotated with {@link Repository} and generate sub-interfaces for those that have
     * Query By Name (QBN) methods.
     * @param annotations the annotations to process
     * @param roundEnv the round environment
     * @return true if the annotations were processed
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        log.debug(String.format("Processing repositories, over=%s\n", roundEnv.processingOver()));
        boolean newRepos = false;
        Set<? extends Element> repositories = roundEnv.getElementsAnnotatedWith(Repository.class);
        for (Element repository : repositories) {
            String provider = repository.getAnnotation(Repository.class).provider();
            if(provider.isEmpty() || provider.equalsIgnoreCase(providerFilter)) {
                String fqn = AnnProcUtils.getFullyQualifiedName(repository);
                log.debug(String.format("Processing repository %s\n", fqn));
                if(repoInfoMap.containsKey(fqn) || repoInfoMap.containsKey(fqn.substring(0, fqn.length()-1))) {
                    log.debug(String.format("Repository(%s) already processed\n", fqn));
                    continue;
                }

                log.debug(String.format("Repository(%s) as kind:%s\n", repository.asType(), repository.getKind()));
                TypeElement entityType = null;
                TypeElement repositoryType = null;
                if(repository instanceof TypeElement) {
                    repositoryType = (TypeElement) repository;
                    entityType = getEntityType(repositoryType);
                    log.debug(String.format("\tRepository(%s) entityType(%s)\n", repository, entityType));
                }
                // If there
                if(entityType == null) {
                    log.debug(String.format("Repository(%s) does not have an JPA entity type\n", repository));
                    continue;
                }
                //
                newRepos |= checkRespositoryForQBN(repositoryType, entityType, processingEnv.getTypeUtils());
            }
        }

        // Generate repository interfaces for QBN methods
        if(newRepos) {
            for (Map.Entry<String, RepositoryInfo> entry : repoInfoMap.entrySet()) {
                RepositoryInfo repoInfo = entry.getValue();
                log.debug(String.format("Generating repository interface for %s\n", entry.getKey()));
                try {
                    AnnProcUtils.writeRepositoryInterface(repoInfo, processingEnv);
                } catch (IOException e) {
                    processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, e.getMessage());
                }
            }
        }
        return true;
    }

    private TypeElement getEntityType(TypeElement repo) {
        if(repo.getQualifiedName().toString().equals("ee.jakarta.tck.data.common.cdi.Directory")) {
            System.out.println("Directory");
        }
        // Check super interfaces for Repository<EntityType>
        for (TypeMirror iface : repo.getInterfaces()) {
            log.debug(String.format("\tRepository(%s) interface(%s)\n", repo, iface));
            if (iface instanceof DeclaredType declaredType) {
                if(!declaredType.getTypeArguments().isEmpty()) {
                    TypeElement candidateType = (TypeElement) processingEnv.getTypeUtils().asElement(declaredType.getTypeArguments().get(0));
                    Entity entity = candidateType.getAnnotation(Entity.class);
                    if (entity != null) {
                        log.debug(String.format("Repository(%s) entityType(%s)\n", repo, candidateType));
                        return candidateType;
                    } else {
                        // Look for custom Entity types based on '*Entity' naming convention
                        // A qualifier annotation would be better, see https://github.com/jakartaee/data/issues/638
                        List<? extends AnnotationMirror> x = candidateType.getAnnotationMirrors();
                        for (AnnotationMirror am : x) {
                            DeclaredType dt = am.getAnnotationType();
                            String annotationName = dt.asElement().getSimpleName().toString();
                            if(annotationName.endsWith("Entity")) {
                                log.debug(String.format("Repository(%s) entityType(%s) from custom annotation:(%s)\n", repo, candidateType, annotationName));
                                return candidateType;
                            }
                        }
                    }
                }
            }
        }
        // Look for lifecycle methods
        for (Element e : repo.getEnclosedElements()) {
            if (e instanceof ExecutableElement ee) {
                if (AnnProcUtils.isLifeCycleMethod(ee)) {
                    List<? extends VariableElement> params = ee.getParameters();
                    for (VariableElement parameter : params) {
                        // Get the type of the parameter
                        TypeMirror parameterType = parameter.asType();

                        if (parameterType instanceof DeclaredType declaredType) {
                            Entity entity = declaredType.getAnnotation(jakarta.persistence.Entity.class);
                            log.debug(String.format("%s, declaredType: %s, entity=%s\n", ee.getSimpleName(), declaredType, entity));
                            if(entity != null) {
                                log.debug(String.format("Repository(%s) entityType(%s)\n", repo, declaredType));
                                return (TypeElement) processingEnv.getTypeUtils().asElement(declaredType);
                            }

                            // Get the type arguments
                            List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

                            for (TypeMirror typeArgument : typeArguments) {
                                TypeElement argType = (TypeElement) processingEnv.getTypeUtils().asElement(typeArgument);
                                Entity entity2 = argType.getAnnotation(jakarta.persistence.Entity.class);
                                log.debug(String.format("%s, typeArgument: %s, entity: %s\n", ee.getSimpleName(), typeArgument, entity2));
                                if(entity2 != null) {
                                    log.debug(String.format("Repository(%s) entityType(%s)\n", repo, typeArgument));
                                    return (TypeElement) processingEnv.getTypeUtils().asElement(typeArgument);
                                }
                            }
                        }
                    }

                }
            }
        }

        return null;
    }


    /**
     * Check a repository for Query By Name methods, and create a {@link RepositoryInfo} object if found.
     * @param repository a repository element
     * @param entityType the entity type for the repository
     * @return true if the repository has QBN methods
     */
    private boolean checkRespositoryForQBN(TypeElement repository, TypeElement entityType, Types types) {
        System.out.println("Checking repository for Query By Name");
        boolean addedRepo = false;

        String entityName = entityType.getQualifiedName().toString();
        List<ExecutableElement> methods = AnnProcUtils.methodsIn(repository);
        RepositoryInfo repoInfo = new RepositoryInfo(repository);
        for (ExecutableElement m : methods) {
            log.debug(String.format("\t%s\n", m.getSimpleName()));
            QueryByNameInfo qbn = AnnProcUtils.isQBN(m);
            if(qbn != null) {
                qbn.setEntity(entityName);
                repoInfo.addQBNMethod(m, qbn, types);
            }

        }
        if(repoInfo.hasQBNMethods()) {
            log.debug(String.format("Repository(%s) has QBN(%d) methods\n", repository, repoInfo.qbnMethods.size()));
            repoInfoMap.put(AnnProcUtils.getFullyQualifiedName(repository), repoInfo);
            addedRepo = true;
        } else {
            log.debug(String.format("Repository(%s) has NO QBN methods\n", repository));
        }
        return addedRepo;
    }

    private void generateQBNRepositoryInterfaces() {
        for (Map.Entry<String, RepositoryInfo> entry : repoInfoMap.entrySet()) {
            RepositoryInfo repoInfo = entry.getValue();
            log.debug(String.format("Generating repository interface for %s\n", entry.getKey()));

        }
    }
}