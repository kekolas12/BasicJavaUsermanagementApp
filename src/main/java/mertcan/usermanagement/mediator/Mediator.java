package mertcan.usermanagement.mediator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Spring tabanlı Mediator implementation'ı
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class Mediator implements IMediator {
    
    private final ApplicationContext applicationContext;
    
    @Override
    @SuppressWarnings("unchecked")
    public <TResponse> TResponse send(IRequest<TResponse> request) {
        Class<?> requestType = request.getClass();
        log.info("Mediator processing request: {}", requestType.getSimpleName());
        
        // Tüm handler'ları al
        Map<String, IRequestHandler> handlers = applicationContext.getBeansOfType(IRequestHandler.class);
        log.info("Found {} handlers in application context", handlers.size());
        
        for (Map.Entry<String, IRequestHandler> entry : handlers.entrySet()) {
            String beanName = entry.getKey();
            IRequestHandler<?, ?> handler = entry.getValue();
            log.info("Checking handler: {} - {}", beanName, handler.getClass().getSimpleName());
            
            // CGLIB proxy class isimlerini düzelt
            String handlerClassName = handler.getClass().getSimpleName();
            // CGLIB proxy ise orijinal class adını al
            if (handlerClassName.contains("$$")) {
                handlerClassName = handlerClassName.substring(0, handlerClassName.indexOf("$$"));
            }
            String expectedHandlerName = requestType.getSimpleName() + "Handler";
            
            log.info("Comparing: '{}' with expected: '{}'", handlerClassName, expectedHandlerName);
            
            if (handlerClassName.equals(expectedHandlerName)) {
                log.info("Found matching handler: {} for request: {}", handlerClassName, requestType.getSimpleName());
                @SuppressWarnings("unchecked")
                IRequestHandler<IRequest<TResponse>, TResponse> typedHandler = 
                    (IRequestHandler<IRequest<TResponse>, TResponse>) handler;
                return typedHandler.handle(request);
            }
        }
        
        // Eğer bulunamazsa daha detaylı hata mesajı ver
        log.error("No handler found for request type: {}", requestType.getSimpleName());
        log.error("Available handlers:");
        for (String beanName : handlers.keySet()) {
            log.error("  - {}: {}", beanName, handlers.get(beanName).getClass().getSimpleName());
        }
        
        throw new RuntimeException("No handler found for request type: " + requestType.getSimpleName());
    }
}
