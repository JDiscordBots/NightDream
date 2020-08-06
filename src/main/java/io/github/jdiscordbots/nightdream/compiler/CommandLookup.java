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
	private void loadClassToFile(TypeElement elem,BufferedWriter writer,String requiredInterface) throws IOException {
		boolean noArgsConstructorFound=false;
		for (Element enclosedElement : elem.getEnclosedElements()) {
			if(enclosedElement.getKind()==ElementKind.CONSTRUCTOR) {
				ExecutableElement constructorElem=(ExecutableElement) enclosedElement;
				if(constructorElem.getParameters().isEmpty()) {
					noArgsConstructorFound=true;
				}
			}
		}
		if(!processingEnv.getTypeUtils().isAssignable(elem.asType(), processingEnv.getElementUtils().getTypeElement(requiredInterface).asType())) {
			processingEnv.getMessager().printMessage(Kind.ERROR, "Missing interface: "+requiredInterface, elem);
		}else if(!noArgsConstructorFound) {
			processingEnv.getMessager().printMessage(Kind.ERROR, "No constructor without parameters found", elem);
		}else{
			writer.write(elem.getQualifiedName().toString());
			writer.write('\n');
		}
	}
	private void loadClassesToFile(String annotationName,BufferedWriter writer,RoundEnvironment roundEnv,String requiredInterface) {
		try{
			TypeElement botCommandAnnotElem = processingEnv.getElementUtils().getTypeElement(annotationName);
			for (Element elem : roundEnv.getElementsAnnotatedWith(botCommandAnnotElem)) {
				if(elem.getKind()==ElementKind.CLASS) {
					TypeElement tElem=(TypeElement) elem;
					loadClassToFile(tElem, writer, requiredInterface);
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
			loadClassesToFile("io.github.jdiscordbots.nightdream.commands.BotCommand", commandListWriter, roundEnv,"io.github.jdiscordbots.nightdream.commands.Command");
			loadClassesToFile("io.github.jdiscordbots.nightdream.listeners.BotListener", listenerListWriter, roundEnv,"net.dv8tion.jda.api.hooks.ListenerAdapter");
		}finally {
			if(roundEnv.processingOver()) {
				try {
					commandListWriter.close();
				} catch (IOException e) {
					processingEnv.getMessager().printMessage(Kind.ERROR, "Cannot close writer for the command list file: "+e.getMessage());
				}
				try {
					listenerListWriter.close();
				} catch (IOException e) {
					processingEnv.getMessager().printMessage(Kind.ERROR, "Cannot close writer for the listener list file: "+e.getMessage());
				}
			}
		}
		return false;
	}
}
