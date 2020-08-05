package io.github.jdiscordbots.nightdream.compiler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import javax.tools.FileObject;
import javax.tools.StandardLocation;

@SupportedAnnotationTypes({"io.github.jdiscordbots.nightdream.commands.BotCommand","io.github.jdiscordbots.nightdream.listeners.BotListener"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CommandLookup extends AbstractProcessor{

	private BufferedWriter commandListWriter;
	private BufferedWriter listenerListWriter;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		try {
			FileObject commandList;
			commandList = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "commands.txt");
			commandListWriter = new BufferedWriter(commandList.openWriter());
			FileObject listenerList = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "listeners.txt");
			listenerListWriter = new BufferedWriter(listenerList.openWriter());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	private void loadClassesToFile(String annotationName,BufferedWriter writer,RoundEnvironment roundEnv) {
		try{
			TypeElement botCommandAnnotElem = processingEnv.getElementUtils().getTypeElement(annotationName);
			for (Element elem : roundEnv.getElementsAnnotatedWith(botCommandAnnotElem)) {
				if(elem.getKind()==ElementKind.CLASS) {
					TypeElement tElem=(TypeElement) elem;
					boolean noArgsConstructorFound=false;
					for (Element typeElement : elem.getEnclosedElements()) {
						if(typeElement.getKind()==ElementKind.CONSTRUCTOR) {
							ExecutableElement constructorElem=(ExecutableElement) typeElement;
							if(constructorElem.getParameters().isEmpty()) {
								noArgsConstructorFound=true;
							}
						}
					}
					if(noArgsConstructorFound) {
						writer.write(tElem.getQualifiedName().toString());
						writer.write('\n');
					}else {
						processingEnv.getMessager().printMessage(Kind.ERROR, "No constructor without parameters found", elem);
					}
				}else {
					processingEnv.getMessager().printMessage(Kind.ERROR, "Non-class annotated with @"+annotationName, elem);
				}
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		try{
			loadClassesToFile("io.github.jdiscordbots.nightdream.commands.BotCommand", commandListWriter, roundEnv);
			loadClassesToFile("io.github.jdiscordbots.nightdream.listeners.BotListener", listenerListWriter, roundEnv);
		}finally {
			if(roundEnv.processingOver()) {
				try {
					commandListWriter.close();
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
				try {
					listenerListWriter.close();
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}
		}
		return false;
	}
}
