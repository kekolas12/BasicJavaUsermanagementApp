package mertcan.usermanagement.mediator;

/**
 * Mediator interface'i - Request'leri uygun handler'lara yönlendirir
 */
public interface IMediator {
    <TResponse> TResponse send(IRequest<TResponse> request);
}
