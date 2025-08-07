package mertcan.usermanagement.mediator;

/**
 * Request handler interface'i
 * @param <TRequest> Request tipi
 * @param <TResponse> Response tipi
 */
public interface IRequestHandler<TRequest extends IRequest<TResponse>, TResponse> {
    TResponse handle(TRequest request);
}
